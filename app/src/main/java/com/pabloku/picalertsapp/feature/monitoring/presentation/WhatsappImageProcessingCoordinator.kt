package com.pabloku.picalertsapp.feature.monitoring.presentation

import com.pabloku.picalertsapp.feature.alerting.domain.AlertEmailPayload
import com.pabloku.picalertsapp.feature.alerting.domain.SendAlertEmailUseCase
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageResult
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class WhatsappImageProcessingCoordinator @Inject constructor(
    private val analyzeImageUseCase: AnalyzeImageUseCase,
    private val getTutorEmailUseCase: GetTutorEmailUseCase,
    private val sendAlertEmailUseCase: SendAlertEmailUseCase
) {

    suspend fun processNewImage(imageFile: File, detectedAt: String) {
        if (!imageFile.exists() || !imageFile.isFile) {
            return
        }

        when (val analysisResult = analyzeImageUseCase(imageFile).getOrElse {
            Timber.w(it, "Unable to analyze image: %s", imageFile.absolutePath)
            return
        }) {
            AnalyzeImageResult.Ok -> Unit
            is AnalyzeImageResult.Flagged -> {
                val guardianEmail = getTutorEmailUseCase().firstOrNull()?.takeIf { it.isNotBlank() }
                    ?: return

                sendAlertEmailUseCase(
                    AlertEmailPayload(
                        guardianEmail = guardianEmail,
                        detectedCategory = analysisResult.detectedCategories.joinToString(", "),
                        detectedAt = detectedAt,
                        imageFile = imageFile
                    )
                )
            }
        }
    }
}
