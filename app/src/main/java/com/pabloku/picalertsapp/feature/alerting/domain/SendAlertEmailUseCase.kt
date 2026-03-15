package com.pabloku.picalertsapp.feature.alerting.domain

import com.pabloku.picalertsapp.BuildConfig
import java.io.File
import java.util.Base64
import javax.inject.Inject

class SendAlertEmailUseCase @Inject constructor(
    private val alertingRepository: AlertingRepository
) {

    suspend operator fun invoke(payload: AlertEmailPayload) {
        runCatching {
            alertingRepository.sendAlertEmail(
                to = payload.guardianEmail,
                from = BuildConfig.RESEND_FROM_EMAIL,
                subject = "Pics Alert: ${payload.detectedCategory} detected",
                html = buildHtml(payload)
            ).getOrThrow()
        }
    }

    private fun buildHtml(payload: AlertEmailPayload): String {
        val thumbnailDataUrl = payload.imageFile.toDataUrl()
        return """
            <html>
              <body style="font-family: Arial, sans-serif; color: #15213D;">
                <h2>Sensitive image detected</h2>
                <p>A potentially dangerous image was detected on the device.</p>
                <p><strong>Category:</strong> ${payload.detectedCategory}</p>
                <p><strong>Detected at:</strong> ${payload.detectedAt}</p>
                <img
                  src="$thumbnailDataUrl"
                  alt="Detected image thumbnail"
                  style="max-width: 240px; border-radius: 12px; border: 1px solid #D9E5FF;"
                />
              </body>
            </html>
        """.trimIndent()
    }

    private fun File.toDataUrl(): String {
        val mimeType = when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
        val base64Content = Base64.getEncoder().encodeToString(readBytes())
        return "data:$mimeType;base64,$base64Content"
    }
}
