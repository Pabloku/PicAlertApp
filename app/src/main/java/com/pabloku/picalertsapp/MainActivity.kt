package com.pabloku.picalertsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pabloku.picalertsapp.ui.theme.PicAlertsAppTheme
import com.pabloku.picalertsapp.feature.onboarding.presentation.OnboardingScreen
import com.pabloku.picalertsapp.feature.onboarding.presentation.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PicAlertsAppTheme {
                val backStack = rememberNavBackStack(MainRoute.Onboarding)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PicAlertsNavDisplay(
                        backStack = backStack,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Serializable
private data object MainRouteOnboarding : NavKey

@Serializable
private data object MainRouteHistory : NavKey

private object MainRoute {
    val Onboarding = MainRouteOnboarding
    val History = MainRouteHistory
}

@Composable
private fun PicAlertsNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainRouteOnboarding> {
                val viewModel = hiltViewModel<OnboardingViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(uiState.isCompleted) {
                    if (uiState.isCompleted) {
                        backStack.add(MainRoute.History)
                        viewModel.onNavigationHandled()
                    }
                }

                OnboardingScreen(
                    uiState = uiState,
                    onEmailChanged = viewModel::onEmailChanged,
                    onConfirmClick = viewModel::onConfirm,
                    modifier = modifier
                )
            }
            entry<MainRouteHistory> {
                MainRouteScreen(
                    title = stringResource(id = R.string.history_title),
                    actionLabel = "Back to onboarding",
                    onActionClick = dropUnlessResumed {
                        backStack.add(MainRoute.Onboarding)
                    },
                    modifier = modifier
                )
            }
        }
    )
}

@Composable
private fun MainRouteScreen(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$title\n$actionLabel",
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    )
}
