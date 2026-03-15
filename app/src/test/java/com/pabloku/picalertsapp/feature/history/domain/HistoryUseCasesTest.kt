package com.pabloku.picalertsapp.feature.history.domain

import com.pabloku.picalertsapp.feature.history.data.AlertEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HistoryUseCasesTest {

    @MockK
    lateinit var historyRepository: HistoryRepository

    private lateinit var saveAlertUseCase: SaveAlertUseCase
    private lateinit var getAlertHistoryUseCase: GetAlertHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        saveAlertUseCase = SaveAlertUseCase(historyRepository)
        getAlertHistoryUseCase = GetAlertHistoryUseCase(historyRepository)
        clearHistoryUseCase = ClearHistoryUseCase(historyRepository)
    }

    @Test
    fun `Given alert details, when save alert is called then persist alert in repository`() = runTest {
        coEvery {
            historyRepository.saveAlert(
                imageUri = "/storage/emulated/0/whatsapp/image.jpg",
                category = "violence",
                timestamp = 1_742_050_000_000
            )
        } returns Unit

        saveAlertUseCase(
            imageUri = "/storage/emulated/0/whatsapp/image.jpg",
            category = "violence",
            timestamp = 1_742_050_000_000
        )

        coVerify(exactly = 1) {
            historyRepository.saveAlert(
                imageUri = "/storage/emulated/0/whatsapp/image.jpg",
                category = "violence",
                timestamp = 1_742_050_000_000
            )
        }
    }

    @Test
    fun `Given stored alerts, when history is observed then return repository alerts`() = runTest {
        val alerts = listOf(
            AlertEntity(id = 1, imageUri = "file://alert.jpg", category = "violence", timestamp = 1_742_050_000_000)
        )
        every { historyRepository.observeAlertHistory() } returns flowOf(alerts)

        val result = getAlertHistoryUseCase().first()

        assertEquals(alerts, result)
    }

    @Test
    fun `Given existing history, when clear history is called then delete alerts from repository`() = runTest {
        coEvery { historyRepository.clearHistory() } returns Unit

        clearHistoryUseCase()

        coVerify(exactly = 1) { historyRepository.clearHistory() }
    }
}
