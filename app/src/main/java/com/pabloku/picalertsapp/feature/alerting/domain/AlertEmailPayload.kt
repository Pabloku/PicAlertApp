package com.pabloku.picalertsapp.feature.alerting.domain

import java.io.File

data class AlertEmailPayload(
    val guardianEmail: String,
    val detectedCategory: String,
    val detectedAt: String,
    val imageFile: File
)
