package com.pabloku.picalertsapp.feature.monitoring.domain

interface MonitoringRepository {
    suspend fun analyzeEncodedImage(encodedImageDataUrl: String): Result<List<String>>
}
