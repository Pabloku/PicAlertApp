package com.pabloku.picalertsapp.feature.monitoring.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.ContentObserver
import android.net.Uri
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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

    private val observerHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var imagesObserver: ContentObserver? = null
    private val handledImageUris = linkedSetOf<String>()
    private var monitoringStartedAtEpochSeconds: Long = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopMonitoring()
            else -> startMonitoring()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        unregisterImagesObserver()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startMonitoring() {
        createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
        registerImagesObserverIfNeeded()
    }

    private fun stopMonitoring() {
        unregisterImagesObserver()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun registerImagesObserverIfNeeded() {
        if (imagesObserver != null) {
            return
        }

        monitoringStartedAtEpochSeconds = System.currentTimeMillis() / 1000
        imagesObserver = object : ContentObserver(observerHandler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                handleImageChange(uri)
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            checkNotNull(imagesObserver)
        )
    }

    private fun unregisterImagesObserver() {
        imagesObserver?.let(contentResolver::unregisterContentObserver)
        imagesObserver = null
    }

    private fun handleImageChange(changedUri: Uri?) {
        val imageUri = changedUri ?: return
        serviceScope.launch {
            val record = resolveMediaImageRecord(imageUri) ?: return@launch

            if (!WhatsappImageChangeEvaluator.isRelevantNewImage(
                    record = record,
                    monitoringStartedAtEpochSeconds = monitoringStartedAtEpochSeconds,
                    alreadyHandledUris = handledImageUris
                )
            ) {
                return@launch
            }

            handledImageUris += imageUri.toString()
            trimHandledUris()

            Timber.d("Detected new WhatsApp image: %s", imageUri)

            record.absolutePath
                ?.takeIf { it.isNotBlank() }
                ?.let { absolutePath ->
                    imageProcessingCoordinator.processNewImage(
                        imageFile = File(absolutePath),
                        detectedAtEpochMillis = record.dateAddedEpochSeconds * 1000,
                        detectedAt = record.formattedDetectedAt()
                    )
                }
        }
    }

    private fun resolveMediaImageRecord(imageUri: Uri): MediaImageRecord? {
        val projection = arrayOf(
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )

        contentResolver.query(imageUri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) {
                return null
            }

            val relativePath = cursor.getStringOrNull(MediaStore.Images.Media.RELATIVE_PATH)
            val absolutePath = cursor.getStringOrNull(MediaStore.Images.Media.DATA)
            val dateAdded = cursor.getLongOrNull(MediaStore.Images.Media.DATE_ADDED) ?: 0L

            return MediaImageRecord(
                contentUri = imageUri.toString(),
                relativePath = relativePath,
                absolutePath = absolutePath,
                dateAddedEpochSeconds = dateAdded
            )
        }

        return null
    }

    private fun trimHandledUris() {
        while (handledImageUris.size > MAX_HANDLED_URI_CACHE_SIZE) {
            handledImageUris.remove(handledImageUris.first())
        }
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
        private const val NOTIFICATION_CHANNEL_ID = "whatsapp_monitoring"
        private const val NOTIFICATION_ID = 1001
        private const val MAX_HANDLED_URI_CACHE_SIZE = 100

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

private fun MediaImageRecord.formattedDetectedAt(): String =
    DETECTED_AT_FORMATTER.format(Instant.ofEpochSecond(dateAddedEpochSeconds))

private fun android.database.Cursor.getStringOrNull(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return if (index >= 0 && !isNull(index)) getString(index) else null
}

private fun android.database.Cursor.getLongOrNull(columnName: String): Long? {
    val index = getColumnIndex(columnName)
    return if (index >= 0 && !isNull(index)) getLong(index) else null
}
