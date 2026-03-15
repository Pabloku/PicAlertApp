package com.pabloku.picalertsapp.feature.monitoring.domain

sealed interface AnalyzeImageResult {
    data object Ok : AnalyzeImageResult

    data class Flagged(
        val detectedCategories: List<String>
    ) : AnalyzeImageResult
}
