package com.pabloku.picalertsapp.feature.onboarding.domain

import com.pabloku.picalertsapp.feature.onboarding.data.TutorEmailLocalDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTutorEmailUseCaseTest {

    @MockK
    lateinit var tutorEmailLocalDataSource: TutorEmailLocalDataSource

    private lateinit var useCase: GetTutorEmailUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetTutorEmailUseCase(tutorEmailLocalDataSource)
    }

    @Test
    fun `Given stored tutor email, when invoke is called then return email from datasource`() =
        runTest {
            every { tutorEmailLocalDataSource.getTutorEmail() } returns flowOf("guardian@example.com")

            val result = useCase().first()

            assertEquals("guardian@example.com", result)
        }
}
