package com.pabloku.picalertsapp.feature.alerting.domain

import timber.log.Timber
import javax.inject.Inject

class SendAlertEmailUseCase @Inject constructor(
    private val alertingRepository: AlertingRepository
) {

    suspend operator fun invoke(payload: AlertEmailPayload): Boolean =
        runCatching {
            alertingRepository.sendAlertEmail(payload).getOrThrow()
            Timber.tag(TAG).i("Alert email repository call succeeded for guardian=%s", payload.guardianEmail)
            true
        }.getOrElse {
            Timber.tag(TAG).e(
                it,
                "Alert email repository call failed for guardian=%s",
                payload.guardianEmail
            )
            false
        }

    private companion object {
        const val TAG = "PicAlertsMonitor"
    }
}
