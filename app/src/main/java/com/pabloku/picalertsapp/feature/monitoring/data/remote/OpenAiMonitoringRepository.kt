package com.pabloku.picalertsapp.feature.monitoring.data.remote

import com.pabloku.picalertsapp.BuildConfig
import com.pabloku.picalertsapp.feature.monitoring.domain.MonitoringRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAiMonitoringRepository @Inject constructor(
    private val openAiApi: OpenAiApi
) : MonitoringRepository {

    override suspend fun analyzeEncodedImage(encodedImageDataUrl: String): Result<List<String>> =
        runCatching {
            val response = openAiApi.createModeration(
                authorization = "Bearer ${BuildConfig.OPENAI_API_KEY}",
                request = OpenAiModerationRequest(
                    input = listOf(
                        OpenAiModerationInput(
                            type = "image_url",
                            imageUrl = OpenAiImageUrl(url = encodedImageDataUrl)
                        )
                    )
                )
            )

            response.results.firstOrNull()
                ?.categories
                ?.detectedCategories()
                .orEmpty()
        }
}
