package com.pabloku.picalertsapp.feature.monitoring.presentation

import java.io.File

object WhatsAppDirectoryLocator {

    fun findReceivedImagesDir(): File? =
        candidateReceivedDirs().firstOrNull { it.exists() && it.isDirectory }

    fun findSentImagesDir(): File? =
        candidateSentDirs().firstOrNull { it.exists() && it.isDirectory }

    fun candidateReceivedDirs(): List<File> = listOf(
        File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images"),
        File("/storage/emulated/0/WhatsApp/Media/WhatsApp Images")
    ).distinctBy { it.absolutePath }

    fun candidateSentDirs(): List<File> = listOf(
        File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/Sent"),
        File("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/Sent")
    ).distinctBy { it.absolutePath }
}
