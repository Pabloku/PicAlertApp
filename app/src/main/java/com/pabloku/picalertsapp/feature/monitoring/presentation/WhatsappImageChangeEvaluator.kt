package com.pabloku.picalertsapp.feature.monitoring.presentation

internal data class MediaImageRecord(
    val contentUri: String,
    val relativePath: String?,
    val absolutePath: String?,
    val dateAddedEpochSeconds: Long
)

internal object WhatsappImageChangeEvaluator {

    private const val WHATSAPP_IMAGES_PATH = "/WhatsApp/Media/WhatsApp Images/"

    fun isRelevantNewImage(
        record: MediaImageRecord,
        monitoringStartedAtEpochSeconds: Long,
        alreadyHandledUris: Set<String>
    ): Boolean {
        if (record.contentUri in alreadyHandledUris) {
            return false
        }

        if (record.dateAddedEpochSeconds < monitoringStartedAtEpochSeconds) {
            return false
        }

        return matchesWhatsappImagesPath(record.relativePath, record.absolutePath)
    }

    fun matchesWhatsappImagesPath(
        relativePath: String?,
        absolutePath: String?
    ): Boolean {
        val normalizedRelativePath = relativePath
            ?.replace('\\', '/')
            ?.trim()
            ?.let { "/$it" }
            ?: ""
        val normalizedAbsolutePath = absolutePath
            ?.replace('\\', '/')
            ?.trim()
            ?: ""

        return normalizedRelativePath.contains(WHATSAPP_IMAGES_PATH, ignoreCase = true) ||
            normalizedAbsolutePath.contains(WHATSAPP_IMAGES_PATH, ignoreCase = true)
    }
}
