package com.pabloku.picalertsapp.feature.alerting.data.model

data class ResendEmailContent(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)
