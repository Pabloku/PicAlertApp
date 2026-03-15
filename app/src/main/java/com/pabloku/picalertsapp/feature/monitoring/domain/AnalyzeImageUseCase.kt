package com.pabloku.picalertsapp.feature.monitoring.domain

import java.io.File
import java.util.Base64
import javax.inject.Inject

class AnalyzeImageUseCase @Inject constructor(
    private val monitoringRepository: MonitoringRepository
) {

    suspend operator fun invoke(imageFile: File): Result<AnalyzeImageResult> {
        val encodedImage = runCatching {
            imageFile.toDataUrl()
        }

        return encodedImage.fold(
            onSuccess = { dataUrl ->
                monitoringRepository.analyzeEncodedImage(dataUrl)
                    .fold(
                        onSuccess = { detectedCategories ->
                            Result.success(
                                if (detectedCategories.isEmpty()) {
                                    AnalyzeImageResult.Ok
                                } else {
                                    AnalyzeImageResult.Flagged(detectedCategories)
                                }
                            )
                        },
                        onFailure = {
                            // MVP policy: if the moderation API fails, treat the image as safe.
                            Result.success(AnalyzeImageResult.Ok)
                        }
                    )
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    private fun File.toDataUrl(): String {
        val mimeType = when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
        val base64Content = Base64.getEncoder().encodeToString(readBytes())
        return "data:$mimeType;base64,$base64Content"
    }
}
