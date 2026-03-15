package com.pabloku.picalertsapp.feature.alerting.data

import com.pabloku.picalertsapp.BuildConfig
import com.pabloku.picalertsapp.feature.alerting.domain.AlertingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResendAlertingRepository @Inject constructor(
    private val resendApi: ResendApi
) : AlertingRepository {

    override suspend fun sendAlertEmail(
        to: String,
        from: String,
        subject: String,
        html: String
    ): Result<Unit> = runCatching {
        resendApi.sendEmail(
            authorization = "Bearer ${BuildConfig.RESEND_API_KEY}",
            request = ResendEmailRequest(
                from = from,
                to = listOf(to),
                subject = subject,
                html = html
            )
        )
        Unit
    }
}
