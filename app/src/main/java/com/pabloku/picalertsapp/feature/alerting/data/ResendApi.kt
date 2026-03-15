package com.pabloku.picalertsapp.feature.alerting.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ResendApi {

    @POST("emails")
    suspend fun sendEmail(
        @Header("Authorization") authorization: String,
        @Body request: ResendEmailRequest
    ): ResendEmailResponse
}

@Serializable
data class ResendEmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

@Serializable
data class ResendEmailResponse(
    val id: String
)
