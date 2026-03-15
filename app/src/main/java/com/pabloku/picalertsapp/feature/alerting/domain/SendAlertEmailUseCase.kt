package com.pabloku.picalertsapp.feature.alerting.domain

import javax.inject.Inject

class SendAlertEmailUseCase @Inject constructor(
    private val alertingRepository: AlertingRepository
) {

    suspend operator fun invoke(payload: AlertEmailPayload) {
        runCatching {
            alertingRepository.sendAlertEmail(payload).getOrThrow()
        }
    }
}
