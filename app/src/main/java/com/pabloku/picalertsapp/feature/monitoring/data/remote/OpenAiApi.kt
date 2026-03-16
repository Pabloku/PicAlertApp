package com.pabloku.picalertsapp.feature.monitoring.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApi {

    @POST("v1/moderations")
    suspend fun createModeration(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiModerationRequest
    ): OpenAiModerationResponse
}

@Serializable
data class OpenAiModerationRequest(
    val model: String = "omni-moderation-latest",
    val input: List<OpenAiModerationInput>
)

@Serializable
data class OpenAiModerationInput(
    val type: String,
    @SerialName("image_url") val imageUrl: OpenAiImageUrl
)

@Serializable
data class OpenAiImageUrl(
    val url: String
)

@Serializable
data class OpenAiModerationResponse(
    val id: String? = null,
    val model: String? = null,
    val results: List<OpenAiModerationResultDto>
)

@Serializable
data class OpenAiModerationResultDto(
    val flagged: Boolean,
    val categories: OpenAiModerationCategoriesDto
)

@Serializable
data class OpenAiModerationCategoriesDto(
    val harassment: Boolean = false,
    @SerialName("harassment/threatening") val harassmentThreatening: Boolean = false,
    val hate: Boolean = false,
    @SerialName("hate/threatening") val hateThreatening: Boolean = false,
    val illicit: Boolean = false,
    @SerialName("illicit/violent") val illicitViolent: Boolean = false,
    @SerialName("self-harm") val selfHarm: Boolean = false,
    @SerialName("self-harm/intent") val selfHarmIntent: Boolean = false,
    @SerialName("self-harm/instructions") val selfHarmInstructions: Boolean = false,
    val sexual: Boolean = false,
    @SerialName("sexual/minors") val sexualMinors: Boolean = false,
    val violence: Boolean = false,
    @SerialName("violence/graphic") val violenceGraphic: Boolean = false
) {
    fun detectedCategories(): List<String> = buildList {
        if (harassment) add("harassment")
        if (harassmentThreatening) add("harassment/threatening")
        if (hate) add("hate")
        if (hateThreatening) add("hate/threatening")
        if (illicit) add("illicit")
        if (illicitViolent) add("illicit/violent")
        if (selfHarm) add("self-harm")
        if (selfHarmIntent) add("self-harm/intent")
        if (selfHarmInstructions) add("self-harm/instructions")
        if (sexual) add("sexual")
        if (sexualMinors) add("sexual/minors")
        if (violence) add("violence")
        if (violenceGraphic) add("violence/graphic")
    }
}
