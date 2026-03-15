package com.pabloku.picalertsapp.feature.alerting.data

import com.pabloku.picalertsapp.BuildConfig
import com.pabloku.picalertsapp.feature.alerting.domain.AlertEmailPayload
import com.pabloku.picalertsapp.feature.alerting.domain.AlertingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResendAlertingRepository @Inject constructor(
    private val resendApi: ResendApi,
    private val resendEmailContentFactory: ResendEmailContentFactory
) : AlertingRepository {

    override suspend fun sendAlertEmail(payload: AlertEmailPayload): Result<Unit> = runCatching {
        val emailContent = resendEmailContentFactory.create(payload)
        resendApi.sendEmail(
            authorization = "Bearer ${BuildConfig.RESEND_API_KEY}",
            request = ResendEmailRequest(
                from = emailContent.from,
                to = emailContent.to,
                subject = emailContent.subject,
                html = emailContent.html
            )
        )
            .also { response ->
                // Keep the API response DTO because Resend returns an id we may use for delivery tracing later.
                check(response.id.isNotBlank())
            }
        Unit
    }
}
