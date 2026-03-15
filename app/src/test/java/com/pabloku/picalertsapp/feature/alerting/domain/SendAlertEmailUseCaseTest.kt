package com.pabloku.picalertsapp.feature.alerting.domain

import com.pabloku.picalertsapp.BuildConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class SendAlertEmailUseCaseTest {

    private val alertingRepository = mockk<AlertingRepository>()
    private val useCase = SendAlertEmailUseCase(alertingRepository)

    @Test
    fun `Given alert payload, when invoke is called then send html email with thumbnail and category`() =
        runTest {
            val imageFile = createTempImageFile("jpg", byteArrayOf(7, 8, 9))
            coEvery {
                alertingRepository.sendAlertEmail(any(), any(), any(), any())
            } returns Result.success(Unit)

            useCase(
                AlertEmailPayload(
                    guardianEmail = "guardian@example.com",
                    detectedCategory = "violence",
                    detectedAt = "2026-03-15 10:30",
                    imageFile = imageFile
                )
            )

            coVerify(exactly = 1) {
                alertingRepository.sendAlertEmail(
                    to = "guardian@example.com",
                    from = BuildConfig.RESEND_FROM_EMAIL,
                    subject = "Pics Alert: violence detected",
                    html = match {
                        it.contains("violence") &&
                            it.contains("2026-03-15 10:30") &&
                            it.contains("data:image/jpeg;base64,")
                    }
                )
            }
        }

    @Test
    fun `Given repository error, when invoke is called then swallow the failure`() = runTest {
        val imageFile = createTempImageFile("png", byteArrayOf(10, 11, 12))
        coEvery {
            alertingRepository.sendAlertEmail(any(), any(), any(), any())
        } throws IllegalStateException("network error")

        useCase(
            AlertEmailPayload(
                guardianEmail = "guardian@example.com",
                detectedCategory = "sexual",
                detectedAt = "2026-03-15 11:00",
                imageFile = imageFile
            )
        )
    }

    private fun createTempImageFile(extension: String, content: ByteArray): File {
        val file = File.createTempFile("alert-image", ".$extension")
        file.writeBytes(content)
        file.deleteOnExit()
        return file
    }
}
