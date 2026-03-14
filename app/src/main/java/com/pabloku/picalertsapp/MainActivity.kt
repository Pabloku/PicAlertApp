package com.pabloku.picalertsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pabloku.picalertsapp.ui.theme.PicAlertsAppTheme
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
    backStack: MutableList<NavKey>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainRouteOnboarding> {
                MainRouteScreen(
                    title = "Onboarding test route",
                    actionLabel = "Go to history",
                    onActionClick = dropUnlessResumed {
                        backStack.add(MainRoute.History)
                    },
                    modifier = modifier
                )
            }
            entry<MainRouteHistory> {
                MainRouteScreen(
                    title = "History test route",
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
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        Button(onClick = onActionClick) {
            Text(text = actionLabel)
        }
    }
}