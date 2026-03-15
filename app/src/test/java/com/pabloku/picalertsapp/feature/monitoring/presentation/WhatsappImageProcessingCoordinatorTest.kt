package com.pabloku.picalertsapp.feature.monitoring.presentation

import com.pabloku.picalertsapp.feature.alerting.domain.AlertEmailPayload
import com.pabloku.picalertsapp.feature.alerting.domain.SendAlertEmailUseCase
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageResult
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File

class WhatsappImageProcessingCoordinatorTest {

    @MockK
    lateinit var analyzeImageUseCase: AnalyzeImageUseCase

    @MockK
    lateinit var getTutorEmailUseCase: GetTutorEmailUseCase

    @MockK
    lateinit var sendAlertEmailUseCase: SendAlertEmailUseCase

    private lateinit var coordinator: WhatsappImageProcessingCoordinator

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coordinator = WhatsappImageProcessingCoordinator(
            analyzeImageUseCase = analyzeImageUseCase,
            getTutorEmailUseCase = getTutorEmailUseCase,
            sendAlertEmailUseCase = sendAlertEmailUseCase
        )
    }

    @Test
    fun `Given safe image result, when processing new image then do not send alert email`() = runTest {
        val imageFile = createTempImageFile()
        coEvery { analyzeImageUseCase(imageFile) } returns Result.success(AnalyzeImageResult.Ok)

        coordinator.processNewImage(imageFile, "2026-03-15 14:50:00")

        coVerify(exactly = 0) { sendAlertEmailUseCase(any()) }
    }

    @Test
    fun `Given flagged image and guardian email, when processing new image then send alert email`() = runTest {
        val imageFile = createTempImageFile()
        coEvery { analyzeImageUseCase(imageFile) } returns
            Result.success(AnalyzeImageResult.Flagged(listOf("violence", "sexual")))
        every { getTutorEmailUseCase() } returns flowOf("guardian@example.com")
        coEvery { sendAlertEmailUseCase(any()) } returns Unit

        coordinator.processNewImage(imageFile, "2026-03-15 14:50:00")

        coVerify(exactly = 1) {
            sendAlertEmailUseCase(
                match { payload: AlertEmailPayload ->
                    payload.guardianEmail == "guardian@example.com" &&
                        payload.detectedCategory == "violence, sexual" &&
                        payload.imageFile == imageFile
                }
            )
        }
    }

    @Test
    fun `Given flagged image without guardian email, when processing new image then do not send alert email`() = runTest {
        val imageFile = createTempImageFile()
        coEvery { analyzeImageUseCase(imageFile) } returns
            Result.success(AnalyzeImageResult.Flagged(listOf("violence")))
        every { getTutorEmailUseCase() } returns flowOf(null)

        coordinator.processNewImage(imageFile, "2026-03-15 14:50:00")

        coVerify(exactly = 0) { sendAlertEmailUseCase(any()) }
    }

    private fun createTempImageFile(): File {
        val file = File.createTempFile("monitoring-image", ".jpg")
        file.writeBytes(byteArrayOf(1, 2, 3))
        file.deleteOnExit()
        return file
    }
}
