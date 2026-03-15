package com.pabloku.picalertsapp.feature.onboarding.presentation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.pabloku.picalertsapp.R
import com.pabloku.picalertsapp.ui.theme.BorderBlue
import com.pabloku.picalertsapp.ui.theme.PicAlertsAppTheme
import com.pabloku.picalertsapp.ui.theme.PrimaryBlue
import com.pabloku.picalertsapp.ui.theme.PrimaryBlueDark
import com.pabloku.picalertsapp.ui.theme.ScreenBackground
import com.pabloku.picalertsapp.ui.theme.TextMuted
import com.pabloku.picalertsapp.ui.theme.TextPrimary
import com.pabloku.picalertsapp.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onEmailChanged: (String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            MainContent(
                uiState = uiState,
                onEmailChanged = onEmailChanged,
                onConfirmClick = onConfirmClick
            )
            Spacer(modifier = Modifier.height(220.dp))
            Text(
                text = stringResource(id = R.string.onboarding_disclaimer),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .fillMaxWidth(0.84f)
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_top_bar_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MainContent(
    uiState: OnboardingUiState,
    onEmailChanged: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = PrimaryBlue
            )
            Text(
                text = stringResource(id = R.string.onboarding_heading),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(id = R.string.onboarding_subheading),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            ),
            modifier = Modifier.fillMaxWidth(0.92f)
        )
        Spacer(modifier = Modifier.height(34.dp))
        Text(
            text = stringResource(id = R.string.onboarding_email_label),
            style = MaterialTheme.typography.titleSmall.copy(
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.isEmailInvalid,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.onboarding_email_placeholder),
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = if (uiState.isEmailInvalid) {
                    MaterialTheme.colorScheme.error
                } else {
                    BorderBlue
                },
                unfocusedBorderColor = if (uiState.isEmailInvalid) {
                    MaterialTheme.colorScheme.error
                } else {
                    BorderBlue
                },
                focusedLeadingIconColor = TextSecondary,
                unfocusedLeadingIconColor = TextSecondary,
                errorLeadingIconColor = MaterialTheme.colorScheme.error
            )
        )
        if (uiState.isEmailInvalid) {
            Text(
                text = stringResource(id = R.string.onboarding_email_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (uiState.isEmailInvalid) 24.dp else 32.dp))
        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_primary_action),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = TextSecondary
            )
            Text(
                text = stringResource(id = R.string.onboarding_security_caption),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 740, widthDp = 360)
@Composable
private fun OnboardingScreenPreview() {
    PicAlertsAppTheme {
        OnboardingScreen(
            uiState = OnboardingUiState(email = "", isEmailInvalid = false),
            onEmailChanged = {},
            onConfirmClick = {},
        )
    }
}
