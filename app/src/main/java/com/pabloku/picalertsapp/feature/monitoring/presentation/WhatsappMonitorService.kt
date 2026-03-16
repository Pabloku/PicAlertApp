package com.pabloku.picalertsapp.feature.monitoring.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.pabloku.picalertsapp.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

private val DETECTED_AT_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())

@AndroidEntryPoint
class WhatsappMonitorService : Service() {

    @Inject
    lateinit var imageProcessingCoordinator: WhatsappImageProcessingCoordinator

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var receivedImagesObserver: WhatsAppDirectoryObserver? = null
    private var sentImagesObserver: WhatsAppDirectoryObserver? = null

    private val handledPaths = linkedSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).i("WhatsappMonitorService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.tag(TAG).i(
            "onStartCommand action=%s startId=%s flags=%s",
            intent?.action,
            startId,
            flags
        )

        when (intent?.action) {
            ACTION_STOP -> stopMonitoring()
            else -> startMonitoring()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Timber.tag(TAG).i("WhatsappMonitorService destroyed")
        stopObservers()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startMonitoring() {
        Timber.tag(TAG).i("Promoting monitoring service to foreground")
        createNotificationChannel()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )

        if (!AllFilesAccessHelper.isGranted()) {
            Timber.tag(TAG).w("Cannot keep monitoring active, all files access not granted")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        Timber.tag(TAG).i("Starting foreground monitoring with all files access")
        startObserversIfNeeded()
    }

    private fun stopMonitoring() {
        Timber.tag(TAG).i("Stopping monitoring")
        stopObservers()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startObserversIfNeeded() {
        if (receivedImagesObserver != null || sentImagesObserver != null) {
            Timber.tag(TAG).d("Observers already started")
            return
        }

        val receivedDir = WhatsAppDirectoryLocator.findReceivedImagesDir()
        val sentDir = WhatsAppDirectoryLocator.findSentImagesDir()

        Timber.tag(TAG).i(
            "Resolved WhatsApp dirs received=%s sent=%s",
            receivedDir?.absolutePath,
            sentDir?.absolutePath
        )

        if (receivedDir == null && sentDir == null) {
            Timber.tag(TAG).w("No WhatsApp image directories found")
            return
        }

        receivedDir?.let { dir ->
            receivedImagesObserver = WhatsAppDirectoryObserver(
                directory = dir,
                observerName = "received",
                onImageReady = { file ->
                    handleObservedFile(file, source = "received")
                }
            ).also { it.start() }
        }

        sentDir?.let { dir ->
            sentImagesObserver = WhatsAppDirectoryObserver(
                directory = dir,
                observerName = "sent",
                onImageReady = { file ->
                    handleObservedFile(file, source = "sent")
                }
            ).also { it.start() }
        }
    }

    private fun stopObservers() {
        receivedImagesObserver?.stop()
        sentImagesObserver?.stop()
        receivedImagesObserver = null
        sentImagesObserver = null
        Timber.tag(TAG).i("All observers stopped")
    }

    private fun handleObservedFile(file: File, source: String) {
        serviceScope.launch {
            val canonicalPath = runCatching { file.canonicalPath }.getOrElse { file.absolutePath }

            if (!markHandled(canonicalPath)) {
                Timber.tag(TAG).d("Ignoring already handled file=%s", canonicalPath)
                return@launch
            }

            if (!file.exists()) {
                Timber.tag(TAG).w("Observed file no longer exists file=%s", canonicalPath)
                return@launch
            }

            if (!file.canRead()) {
                Timber.tag(TAG).w("Observed file is not readable file=%s", canonicalPath)
                return@launch
            }

            if (file.length() <= 0L) {
                Timber.tag(TAG).w("Observed file has size 0 file=%s", canonicalPath)
                return@launch
            }

            val detectedAtEpochMillis =
                file.lastModified().takeIf { it > 0L } ?: System.currentTimeMillis()
            val detectedAt = DETECTED_AT_FORMATTER.format(
                Instant.ofEpochMilli(detectedAtEpochMillis)
            )

            Timber.tag(TAG).i(
                "Processing WhatsApp image source=%s file=%s size=%s lastModified=%s",
                source,
                canonicalPath,
                file.length(),
                detectedAtEpochMillis
            )

            imageProcessingCoordinator.processNewImage(
                imageFile = file,
                detectedAtEpochMillis = detectedAtEpochMillis,
                detectedAt = detectedAt
            )
        }
    }

    private fun markHandled(path: String): Boolean {
        val added = handledPaths.add(path)
        while (handledPaths.size > MAX_HANDLED_PATHS) {
            handledPaths.remove(handledPaths.first())
        }
        return added
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.monitoring_notification_title))
            .setContentText(getString(R.string.monitoring_notification_text))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.monitoring_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.monitoring_notification_channel_description)
        }

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "PicAlertsMonitor"
        private const val NOTIFICATION_CHANNEL_ID = "whatsapp_monitoring"
        private const val NOTIFICATION_ID = 1001
        private const val MAX_HANDLED_PATHS = 200

        const val ACTION_START = "com.pabloku.picalertsapp.action.START_MONITORING"
        const val ACTION_STOP = "com.pabloku.picalertsapp.action.STOP_MONITORING"

        fun startIntent(context: Context): Intent =
            Intent(context, WhatsappMonitorService::class.java).apply {
                action = ACTION_START
            }

        fun stopIntent(context: Context): Intent =
            Intent(context, WhatsappMonitorService::class.java).apply {
                action = ACTION_STOP
            }
    }
}
