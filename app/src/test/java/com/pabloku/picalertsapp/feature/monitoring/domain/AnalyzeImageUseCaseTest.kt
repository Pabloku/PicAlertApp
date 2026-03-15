package com.pabloku.picalertsapp.feature.monitoring.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AnalyzeImageUseCaseTest {

    private val monitoringRepository = mockk<MonitoringRepository>()
    private val useCase = AnalyzeImageUseCase(monitoringRepository)

    @Test
    fun `Given safe image response, when invoke is called then return ok state`() = runTest {
        val imageFile = createTempImageFile("jpg", byteArrayOf(1, 2, 3))
        coEvery { monitoringRepository.analyzeEncodedImage(any()) } returns Result.success(emptyList())

        val result = useCase(imageFile)

        assertTrue(result.isSuccess)
        assertEquals(AnalyzeImageResult.Ok, result.getOrNull())
        coVerify(exactly = 1) { monitoringRepository.analyzeEncodedImage(match { it.startsWith("data:image/jpeg;base64,") }) }
    }

    @Test
    fun `Given flagged image response, when invoke is called then return detected categories`() = runTest {
        val imageFile = createTempImageFile("png", byteArrayOf(4, 5, 6))
        coEvery { monitoringRepository.analyzeEncodedImage(any()) } returns
            Result.success(listOf("violence", "sexual"))

        val result = useCase(imageFile)

        assertEquals(
            AnalyzeImageResult.Flagged(listOf("violence", "sexual")),
            result.getOrNull()
        )
    }

    private fun createTempImageFile(extension: String, content: ByteArray): File {
        val file = File.createTempFile("test-image", ".$extension")
        file.writeBytes(content)
        file.deleteOnExit()
        return file
    }
}
