package com.pabloku.picalertsapp.feature.onboarding.domain

import com.pabloku.picalertsapp.feature.onboarding.data.TutorEmailLocalDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveTutorEmailUseCaseTest {

    @MockK
    lateinit var tutorEmailLocalDataSource: TutorEmailLocalDataSource

    private lateinit var useCase: SaveTutorEmailUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SaveTutorEmailUseCase(tutorEmailLocalDataSource)
    }

    @Test
    fun `Given invalid email, when invoke is called then do not save email and return false`() =
        runTest {
            val result = useCase("not-an-email")

            assertFalse(result)
            coVerify(exactly = 0) { tutorEmailLocalDataSource.saveTutorEmail(any()) }
        }

    @Test
    fun `Given email with surrounding whitespace, when invoke is called then trim email save it and return true`() =
        runTest {
            coEvery { tutorEmailLocalDataSource.saveTutorEmail("tutor@example.com") } returns Unit

            val result = useCase("  tutor@example.com  ")

            assertTrue(result)
            coVerify(exactly = 1) { tutorEmailLocalDataSource.saveTutorEmail("tutor@example.com") }
        }
}
