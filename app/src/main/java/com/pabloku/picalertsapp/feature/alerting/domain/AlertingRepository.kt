package com.pabloku.picalertsapp.feature.alerting.domain

interface AlertingRepository {
    suspend fun sendAlertEmail(payload: AlertEmailPayload): Result<Unit>
}
