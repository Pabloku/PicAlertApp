package com.pabloku.picalertsapp.feature.history.presentation

import com.pabloku.picalertsapp.feature.history.data.AlertEntity
import com.pabloku.picalertsapp.feature.history.domain.ClearHistoryUseCase
import com.pabloku.picalertsapp.feature.history.domain.GetAlertHistoryUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @MockK
    lateinit var getAlertHistoryUseCase: GetAlertHistoryUseCase

    @MockK
    lateinit var clearHistoryUseCase: ClearHistoryUseCase

    @MockK
    lateinit var getTutorEmailUseCase: GetTutorEmailUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given stored alerts, when view model is created then expose mapped history ui state`() = runTest {
        every { getAlertHistoryUseCase() } returns flowOf(
            listOf(
                AlertEntity(
                    id = 1,
                    imageUri = "/storage/emulated/0/alert.jpg",
                    category = "violence",
                    timestamp = 1_742_050_000_000
                ),
                AlertEntity(
                    id = 2,
                    imageUri = "/storage/emulated/0/alert-2.jpg",
                    category = "sexual",
                    timestamp = 1_741_963_600_000
                )
            )
        )
        every { getTutorEmailUseCase() } returns flowOf("guardian@example.com")

        val viewModel = HistoryViewModel(
            getAlertHistoryUseCase = getAlertHistoryUseCase,
            getTutorEmailUseCase = getTutorEmailUseCase,
            clearHistoryUseCase = clearHistoryUseCase
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.alerts.size)
        assertEquals("guardian@example.com", viewModel.uiState.value.tutorEmail)
        assertEquals("VIOLENCE", viewModel.uiState.value.alerts.first().categoryLabel)
        assertEquals("ADULT CONTENT", viewModel.uiState.value.alerts[1].categoryLabel)
        assertFalse(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `Given alert history screen, when clear history is clicked then clear use case is invoked`() = runTest {
        every { getAlertHistoryUseCase() } returns flowOf(emptyList())
        every { getTutorEmailUseCase() } returns flowOf("guardian@example.com")
        coEvery { clearHistoryUseCase() } returns Unit
        val viewModel = HistoryViewModel(
            getAlertHistoryUseCase = getAlertHistoryUseCase,
            getTutorEmailUseCase = getTutorEmailUseCase,
            clearHistoryUseCase = clearHistoryUseCase
        )
        advanceUntilIdle()

        viewModel.onClearHistoryClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { clearHistoryUseCase() }
    }
}
