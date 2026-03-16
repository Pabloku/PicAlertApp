package com.pabloku.picalertsapp.feature.monitoring.presentation

import android.os.FileObserver
import java.io.File
import timber.log.Timber

class WhatsAppDirectoryObserver(
    private val directory: File,
    private val observerName: String,
    private val onImageReady: (File) -> Unit
) {
    private var observer: FileObserver? = null
    private val recentlyEmitted = linkedMapOf<String, Long>()

    fun start() {
        if (observer != null) {
            Timber.tag(TAG).d(
                "start() ignored, observer already started name=%s dir=%s",
                observerName,
                directory.absolutePath
            )
            return
        }

        if (!directory.exists() || !directory.isDirectory) {
            Timber.tag(TAG).w(
                "Directory missing or invalid name=%s dir=%s",
                observerName,
                directory.absolutePath
            )
            return
        }

        Timber.tag(TAG).i(
            "Starting FileObserver name=%s dir=%s",
            observerName,
            directory.absolutePath
        )

        observer = object : FileObserver(directory, ALL_RELEVANT_EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                try {
                    val eventType = event and ALL_RELEVANT_EVENTS
                    val eventName = eventToString(eventType)

                    Timber.tag(TAG).d(
                        "onEvent name=%s event=%s raw=%s path=%s dir=%s",
                        observerName,
                        eventName,
                        event,
                        path,
                        directory.absolutePath
                    )

                    if (path.isNullOrBlank()) {
                        Timber.tag(TAG).d("Ignored null/blank path name=%s", observerName)
                        return
                    }

                    val file = File(directory, path)

                    if (!looksLikeImage(file)) {
                        Timber.tag(TAG).d(
                            "Ignored non-image name=%s file=%s",
                            observerName,
                            file.absolutePath
                        )
                        return
                    }

                    if (eventType == FileObserver.MOVED_FROM) {
                        Timber.tag(TAG).d(
                            "Ignored MOVED_FROM name=%s file=%s",
                            observerName,
                            file.absolutePath
                        )
                        return
                    }

                    if (!waitUntilStable(file)) {
                        Timber.tag(TAG).w(
                            "File never stabilized name=%s file=%s",
                            observerName,
                            file.absolutePath
                        )
                        return
                    }

                    val canonicalPath =
                        runCatching { file.canonicalPath }.getOrElse { file.absolutePath }
                    val now = System.currentTimeMillis()
                    val lastEmission = recentlyEmitted[canonicalPath]

                    if (lastEmission != null && now - lastEmission < DUPLICATE_WINDOW_MS) {
                        Timber.tag(TAG).d(
                            "Ignored duplicate event name=%s file=%s deltaMs=%s",
                            observerName,
                            canonicalPath,
                            now - lastEmission
                        )
                        return
                    }

                    recentlyEmitted[canonicalPath] = now
                    trimRecent()

                    Timber.tag(TAG).i(
                        "Accepted image event name=%s event=%s file=%s size=%s",
                        observerName,
                        eventName,
                        canonicalPath,
                        file.length()
                    )

                    onImageReady(file)
                } catch (t: Throwable) {
                    Timber.tag(TAG).e(
                        t,
                        "FileObserver failure name=%s dir=%s",
                        observerName,
                        directory.absolutePath
                    )
                }
            }
        }.also {
            it.startWatching()
        }

        Timber.tag(TAG).i(
            "FileObserver started name=%s dir=%s",
            observerName,
            directory.absolutePath
        )
    }

    fun stop() {
        observer?.stopWatching()
        observer = null
        Timber.tag(TAG).i(
            "FileObserver stopped name=%s dir=%s",
            observerName,
            directory.absolutePath
        )
    }

    private fun looksLikeImage(file: File): Boolean {
        if (!file.name.contains('.')) return false
        return file.extension.lowercase() in IMAGE_EXTENSIONS
    }

    private fun waitUntilStable(file: File): Boolean {
        var previousSize = -1L

        repeat(STABILITY_CHECK_ATTEMPTS) { attempt ->
            if (!file.exists() || !file.isFile) {
                return false
            }

            val currentSize = file.length()

            Timber.tag(TAG).d(
                "Stability check name=%s file=%s attempt=%s size=%s previousSize=%s",
                observerName,
                file.absolutePath,
                attempt + 1,
                currentSize,
                previousSize
            )

            if (currentSize > 0L && currentSize == previousSize) {
                return true
            }

            previousSize = currentSize
            Thread.sleep(STABILITY_CHECK_DELAY_MS)
        }

        return file.exists() && file.isFile && file.length() > 0L
    }

    private fun trimRecent() {
        while (recentlyEmitted.size > MAX_RECENTLY_EMITTED) {
            val firstKey = recentlyEmitted.entries.iterator().next().key
            recentlyEmitted.remove(firstKey)
        }
    }

    private fun eventToString(event: Int): String =
        when (event) {
            FileObserver.CREATE -> "CREATE"
            FileObserver.CLOSE_WRITE -> "CLOSE_WRITE"
            FileObserver.MOVED_TO -> "MOVED_TO"
            FileObserver.MOVED_FROM -> "MOVED_FROM"
            else -> "OTHER"
        }

    companion object {
        private const val TAG = "WADirObserver"
        private const val DUPLICATE_WINDOW_MS = 1500L
        private const val STABILITY_CHECK_DELAY_MS = 250L
        private const val STABILITY_CHECK_ATTEMPTS = 6
        private const val MAX_RECENTLY_EMITTED = 200

        private val ALL_RELEVANT_EVENTS: Int =
            FileObserver.CREATE or
                    FileObserver.CLOSE_WRITE or
                    FileObserver.MOVED_TO or
                    FileObserver.MOVED_FROM

        private val IMAGE_EXTENSIONS = setOf(
            "jpg", "jpeg", "png", "webp", "heic"
        )
    }
}
