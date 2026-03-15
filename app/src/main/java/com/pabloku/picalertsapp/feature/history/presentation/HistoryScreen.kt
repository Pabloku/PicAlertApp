package com.pabloku.picalertsapp.feature.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.pabloku.picalertsapp.R
import com.pabloku.picalertsapp.feature.history.presentation.model.AlertHistoryItemUiModel
import com.pabloku.picalertsapp.ui.theme.PicAlertsAppTheme
import com.pabloku.picalertsapp.ui.theme.PrimaryBlue
import com.pabloku.picalertsapp.ui.theme.ScreenBackground
import com.pabloku.picalertsapp.ui.theme.SurfaceWhite
import com.pabloku.picalertsapp.ui.theme.TextMuted
import com.pabloku.picalertsapp.ui.theme.TextPrimary
import com.pabloku.picalertsapp.ui.theme.TextSecondary

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onClearHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HistoryHeader(
                    showClearAction = uiState.alerts.isNotEmpty(),
                    onClearHistoryClick = onClearHistoryClick
                )
            }
            item {
                MonitoringStatusCard()
            }
            item {
                Text(
                    text = stringResource(id = R.string.history_recent_notifications),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.4.sp
                    )
                )
            }

            if (uiState.isEmpty) {
                item {
                    HistoryEmptyState()
                }
            } else {
                items(uiState.alerts, key = { alert -> alert.id }) { alert ->
                    AlertHistoryCard(alert = alert)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun HistoryHeader(
    showClearAction: Boolean,
    onClearHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(id = R.string.history_screen_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
        if (showClearAction) {
            IconButton(onClick = onClearHistoryClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.history_clear_action),
                    tint = TextSecondary
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun MonitoringStatusCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7FBF4)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF15B77B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(id = R.string.history_monitoring_active_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF17825E),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.history_monitoring_active_body),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF3D8A72),
                            lineHeight = 18.sp
                        )
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.ToggleOn,
                contentDescription = null,
                tint = Color(0xFF13B67D),
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
private fun AlertHistoryCard(
    alert: AlertHistoryItemUiModel
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlertThumbnail(categoryLabel = alert.categoryLabel)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChip(label = alert.categoryLabel)
                    Text(
                        text = alert.timestampLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = alert.summary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String) {
    val (containerColor, contentColor) = when (label) {
        "VIOLENCE" -> Color(0xFFFFE2DF) to Color(0xFFE04C3D)
        "ADULT CONTENT" -> Color(0xFFFFF0DA) to Color(0xFFDA7A18)
        else -> Color(0xFFFFF4CF) to Color(0xFFBE7F12)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Composable
private fun AlertThumbnail(categoryLabel: String) {
    val colors = when (categoryLabel) {
        "VIOLENCE" -> listOf(Color(0xFF7A8330), Color(0xFF27320D))
        "ADULT CONTENT" -> listOf(Color(0xFF8A7A2F), Color(0xFF213617))
        else -> listOf(Color(0xFFD2D5DA), Color(0xFF515C68))
    }
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.VisibilityOff,
            contentDescription = null,
            tint = SurfaceWhite.copy(alpha = 0.92f),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun HistoryEmptyState() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            }
            Text(
                text = stringResource(id = R.string.history_empty_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = stringResource(id = R.string.history_empty_body),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary,
                    lineHeight = 24.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun HistoryScreenPreview() {
    PicAlertsAppTheme {
        HistoryScreen(
            uiState = HistoryUiState(
                alerts = listOf(
                    AlertHistoryItemUiModel(
                        id = 1,
                        imageUri = "file://alert-1.jpg",
                        categoryLabel = "VIOLENCE",
                        summary = "Potential harmful content detected in a recent WhatsApp media attachment.",
                        timestampLabel = "TODAY, 14:32"
                    ),
                    AlertHistoryItemUiModel(
                        id = 2,
                        imageUri = "file://alert-2.jpg",
                        categoryLabel = "ADULT CONTENT",
                        summary = "Sensitive image detected in a WhatsApp chat and an alert email was sent.",
                        timestampLabel = "TODAY, 09:15"
                    )
                )
            ),
            onClearHistoryClick = {}
        )
    }
}
