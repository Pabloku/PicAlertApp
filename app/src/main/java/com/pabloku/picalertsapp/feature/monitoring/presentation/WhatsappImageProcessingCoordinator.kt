package com.pabloku.picalertsapp.feature.monitoring.presentation

import com.pabloku.picalertsapp.feature.alerting.domain.AlertEmailPayload
import com.pabloku.picalertsapp.feature.alerting.domain.SendAlertEmailUseCase
import com.pabloku.picalertsapp.feature.history.domain.SaveAlertUseCase
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageResult
import com.pabloku.picalertsapp.feature.monitoring.domain.AnalyzeImageUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class WhatsappImageProcessingCoordinator @Inject constructor(
    private val analyzeImageUseCase: AnalyzeImageUseCase,
    private val getTutorEmailUseCase: GetTutorEmailUseCase,
    private val sendAlertEmailUseCase: SendAlertEmailUseCase,
    private val saveAlertUseCase: SaveAlertUseCase
) {

    suspend fun processNewImage(
        imageFile: File,
        detectedAtEpochMillis: Long,
        detectedAt: String
    ) {
        if (!imageFile.exists() || !imageFile.isFile) {
            Timber.tag(TAG).w(
                "Skipping image processing because file is missing: %s",
                imageFile.absolutePath
            )
            return
        }

        Timber.tag(TAG).i(
            "Processing image file=%s detectedAt=%s detectedAtEpochMillis=%s",
            imageFile.absolutePath,
            detectedAt,
            detectedAtEpochMillis
        )

        when (val analysisResult = analyzeImageUseCase(imageFile).getOrElse {
            Timber.tag(TAG).w(it, "Unable to analyze image: %s", imageFile.absolutePath)
            return
        }) {
            AnalyzeImageResult.Ok -> {
                Timber.tag(TAG).i("Image marked safe by moderation: %s", imageFile.absolutePath)
            }

            is AnalyzeImageResult.Flagged -> {
                Timber.tag(TAG).i(
                    "Image flagged categories=%s file=%s",
                    analysisResult.detectedCategories.joinToString(),
                    imageFile.absolutePath
                )
                val guardianEmail = getTutorEmailUseCase().firstOrNull()?.takeIf { it.isNotBlank() }
                    ?: run {
                        Timber.tag(TAG).w("No guardian email configured, skipping alert send")
                        return
                    }

                val detectedCategory = analysisResult.detectedCategories.joinToString(", ")
                Timber.tag(TAG).i(
                    "Sending alert email to guardian=%s category=%s",
                    guardianEmail,
                    detectedCategory
                )
                val didSend = sendAlertEmailUseCase(
                    AlertEmailPayload(
                        guardianEmail = guardianEmail,
                        detectedCategory = detectedCategory,
                        detectedAt = detectedAt,
                        imageFile = imageFile
                    )
                )
                if (didSend) {
                    Timber.tag(TAG).i("Alert email sent successfully, saving alert history entry")
                    saveAlertUseCase(
                        imageUri = imageFile.absolutePath,
                        category = detectedCategory,
                        timestamp = detectedAtEpochMillis
                    )
                } else {
                    Timber.tag(TAG).w("Alert email send failed for file=%s", imageFile.absolutePath)
                }
            }
        }
    }

    private companion object {
        const val TAG = "PicAlertsMonitor"
    }
}
