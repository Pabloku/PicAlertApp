package com.pabloku.picalertsapp.feature.onboarding.presentation

import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.SaveTutorEmailUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @MockK
    lateinit var getTutorEmailUseCase: GetTutorEmailUseCase

    @MockK
    lateinit var saveTutorEmailUseCase: SaveTutorEmailUseCase

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
    fun `Given stored tutor email, when view model is created then populate ui state with stored email`() =
        runTest {
            every { getTutorEmailUseCase() } returns flowOf("guardian@example.com")

            val viewModel = OnboardingViewModel(
                getTutorEmailUseCase = getTutorEmailUseCase,
                saveTutorEmailUseCase = saveTutorEmailUseCase
            )
            advanceUntilIdle()

            assertEquals("guardian@example.com", viewModel.uiState.value.email)
            assertFalse(viewModel.uiState.value.isEmailInvalid)
            assertFalse(viewModel.uiState.value.isCompleted)
        }

    @Test
    fun `Given email input change, when email is updated then clear invalid state and update ui state`() =
        runTest {
            every { getTutorEmailUseCase() } returns flowOf(null)
            val viewModel = OnboardingViewModel(
                getTutorEmailUseCase = getTutorEmailUseCase,
                saveTutorEmailUseCase = saveTutorEmailUseCase
            )
            advanceUntilIdle()

            viewModel.onEmailChanged("parent@example.com")

            assertEquals("parent@example.com", viewModel.uiState.value.email)
            assertFalse(viewModel.uiState.value.isEmailInvalid)
        }

    @Test
    fun `Given invalid email, when confirm is called then expose invalid state and do not complete`() =
        runTest {
            every { getTutorEmailUseCase() } returns flowOf(null)
            coEvery { saveTutorEmailUseCase("invalid-email") } returns false
            val viewModel = OnboardingViewModel(
                getTutorEmailUseCase = getTutorEmailUseCase,
                saveTutorEmailUseCase = saveTutorEmailUseCase
            )
            advanceUntilIdle()
            viewModel.onEmailChanged("invalid-email")

            viewModel.onConfirm()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isEmailInvalid)
            assertFalse(viewModel.uiState.value.isCompleted)
            coVerify(exactly = 1) { saveTutorEmailUseCase("invalid-email") }
        }

    @Test
    fun `Given valid email, when confirm is called then complete onboarding`() = runTest {
        val storedEmailFlow = MutableStateFlow<String?>(null)
        every { getTutorEmailUseCase() } returns storedEmailFlow
        coEvery { saveTutorEmailUseCase("parent@example.com") } returns true
        val viewModel = OnboardingViewModel(
            getTutorEmailUseCase = getTutorEmailUseCase,
            saveTutorEmailUseCase = saveTutorEmailUseCase
        )
        advanceUntilIdle()
        viewModel.onEmailChanged("parent@example.com")

        viewModel.onConfirm()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isEmailInvalid)
        assertTrue(viewModel.uiState.value.isCompleted)
        coVerify(exactly = 1) { saveTutorEmailUseCase("parent@example.com") }
    }

    @Test
    fun `Given completed onboarding, when navigation is handled then reset completion flag`() = runTest {
        every { getTutorEmailUseCase() } returns flowOf(null)
        coEvery { saveTutorEmailUseCase("parent@example.com") } returns true
        val viewModel = OnboardingViewModel(
            getTutorEmailUseCase = getTutorEmailUseCase,
            saveTutorEmailUseCase = saveTutorEmailUseCase
        )
        advanceUntilIdle()
        viewModel.onEmailChanged("parent@example.com")
        viewModel.onConfirm()
        advanceUntilIdle()

        viewModel.onNavigationHandled()

        assertFalse(viewModel.uiState.value.isCompleted)
    }
}
