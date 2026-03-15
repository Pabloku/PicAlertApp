package com.pabloku.picalertsapp.feature.alerting.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SendAlertEmailUseCaseTest {

    private val alertingRepository = mockk<AlertingRepository>()
    private val useCase = SendAlertEmailUseCase(alertingRepository)

    @Test
    fun `Given alert payload, when invoke is called then send html email with thumbnail and category`() =
        runTest {
            val imageFile = createTempImageFile("jpg", byteArrayOf(7, 8, 9))
            val payload = AlertEmailPayload(
                guardianEmail = "guardian@example.com",
                detectedCategory = "violence",
                detectedAt = "2026-03-15 10:30",
                imageFile = imageFile
            )
            coEvery { alertingRepository.sendAlertEmail(payload) } returns Result.success(Unit)

            val result = useCase(payload)

            assertTrue(result)
            coVerify(exactly = 1) { alertingRepository.sendAlertEmail(payload) }
        }

    @Test
    fun `Given repository error, when invoke is called then swallow the failure`() = runTest {
        val imageFile = createTempImageFile("png", byteArrayOf(10, 11, 12))
        val payload = AlertEmailPayload(
            guardianEmail = "guardian@example.com",
            detectedCategory = "sexual",
            detectedAt = "2026-03-15 11:00",
            imageFile = imageFile
        )
        coEvery { alertingRepository.sendAlertEmail(payload) } returns
            Result.failure(IllegalStateException("network error"))

        val result = useCase(payload)

        assertFalse(result)
    }

    private fun createTempImageFile(extension: String, content: ByteArray): File {
        val file = File.createTempFile("alert-image", ".$extension")
        file.writeBytes(content)
        file.deleteOnExit()
        return file
    }
}
