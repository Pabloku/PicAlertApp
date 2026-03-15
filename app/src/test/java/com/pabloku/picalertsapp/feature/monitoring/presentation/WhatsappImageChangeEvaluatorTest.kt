package com.pabloku.picalertsapp.feature.monitoring.presentation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsappImageChangeEvaluatorTest {

    @Test
    fun `Given whatsapp relative path, when path is evaluated then return true`() {
        val result = WhatsappImageChangeEvaluator.matchesWhatsappImagesPath(
            relativePath = "WhatsApp/Media/WhatsApp Images/",
            absolutePath = null
        )

        assertTrue(result)
    }

    @Test
    fun `Given non whatsapp path, when path is evaluated then return false`() {
        val result = WhatsappImageChangeEvaluator.matchesWhatsappImagesPath(
            relativePath = "Pictures/Screenshots/",
            absolutePath = "/storage/emulated/0/Pictures/Screenshots/image.jpg"
        )

        assertFalse(result)
    }

    @Test
    fun `Given old image record, when new image relevance is evaluated then return false`() {
        val result = WhatsappImageChangeEvaluator.isRelevantNewImage(
            record = MediaImageRecord(
                contentUri = "content://media/external/images/media/1",
                relativePath = "WhatsApp/Media/WhatsApp Images/",
                absolutePath = null,
                dateAddedEpochSeconds = 100
            ),
            monitoringStartedAtEpochSeconds = 200,
            alreadyHandledUris = emptySet()
        )

        assertFalse(result)
    }

    @Test
    fun `Given already handled whatsapp image, when new image relevance is evaluated then return false`() {
        val uri = "content://media/external/images/media/5"
        val result = WhatsappImageChangeEvaluator.isRelevantNewImage(
            record = MediaImageRecord(
                contentUri = uri,
                relativePath = "WhatsApp/Media/WhatsApp Images/",
                absolutePath = null,
                dateAddedEpochSeconds = 300
            ),
            monitoringStartedAtEpochSeconds = 200,
            alreadyHandledUris = setOf(uri)
        )

        assertFalse(result)
    }

    @Test
    fun `Given new whatsapp image, when new image relevance is evaluated then return true`() {
        val result = WhatsappImageChangeEvaluator.isRelevantNewImage(
            record = MediaImageRecord(
                contentUri = "content://media/external/images/media/10",
                relativePath = "WhatsApp/Media/WhatsApp Images/",
                absolutePath = null,
                dateAddedEpochSeconds = 300
            ),
            monitoringStartedAtEpochSeconds = 200,
            alreadyHandledUris = emptySet()
        )

        assertTrue(result)
    }
}
