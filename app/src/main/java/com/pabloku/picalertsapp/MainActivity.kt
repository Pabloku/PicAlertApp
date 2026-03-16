package com.pabloku.picalertsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pabloku.picalertsapp.feature.history.presentation.HistoryScreen
import com.pabloku.picalertsapp.feature.history.presentation.HistoryViewModel
import com.pabloku.picalertsapp.feature.monitoring.presentation.WhatsappMonitorService
import com.pabloku.picalertsapp.ui.theme.PicAlertsAppTheme
import com.pabloku.picalertsapp.feature.onboarding.presentation.OnboardingScreen
import com.pabloku.picalertsapp.feature.onboarding.presentation.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PicAlertsAppTheme {
                val mainViewModel = hiltViewModel<MainViewModel>()
                val mainUiState by mainViewModel.uiState.collectAsState()

                if (mainUiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val initialRoute = if (mainUiState.hasTutorEmail) {
                        Timber.tag(MAIN_TAG).i("Starting app on history route")
                        MainRoute.History
                    } else {
                        Timber.tag(MAIN_TAG).i("Starting app on onboarding route")
                        MainRoute.Onboarding
                    }
                    val backStack = rememberNavBackStack(initialRoute)
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
                        while (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
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
                val context = LocalContext.current
                val viewModel = hiltViewModel<HistoryViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                HistoryScreen(
                    uiState = uiState,
                    onClearHistoryClick = viewModel::onClearHistoryClick,
                    onChangeEmailClick = {
                        Timber.tag(MAIN_TAG).i("Navigating from history to onboarding for email change")
                        runCatching {
                            context.startService(WhatsappMonitorService.stopIntent(context))
                        }.onFailure { throwable ->
                            Timber.tag(MAIN_TAG).e(throwable, "Failed to stop monitoring before email change")
                        }
                        while (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(MainRoute.Onboarding)
                    },
                    modifier = modifier
                )
            }
        }
    )
}

private const val MAIN_TAG = "PicAlertsMonitor"
