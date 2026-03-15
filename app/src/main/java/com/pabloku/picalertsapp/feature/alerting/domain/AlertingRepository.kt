package com.pabloku.picalertsapp.feature.alerting.domain

interface AlertingRepository {
    suspend fun sendAlertEmail(
        to: String,
        from: String,
        subject: String,
        html: String
    ): Result<Unit>
}
