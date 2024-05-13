package ru.control24.tracking.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.control24.tracking.presentation.navigation.root.RootNavigationGraph
import ru.control24.tracking.presentation.ui.components.CustomDialog
import ru.control24.tracking.presentation.ui.theme.Control24Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.holdSplash
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Control24Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigationGraph(
                        navController = rememberNavController(),
                        mainViewModel = mainViewModel
                    )
                }
                CustomDialog(
                    iconRes = mainViewModel.messageDialogState.iconRes,
                    titleRes = mainViewModel.messageDialogState.titleRes,
                    messageRes = mainViewModel.messageDialogState.messageRes,
                    messageString = mainViewModel.messageDialogState.messageString,
                    onDismissRequest = { mainViewModel.uiEvent(UiEvent.CloseMessageDialog) },
                    dismissTextRes = mainViewModel.messageDialogState.dismissTextRes,
                    onDismiss = mainViewModel.messageDialogState.onDismiss,
                    confirmTextRes = mainViewModel.messageDialogState.confirmTextRes,
                    onConfirm = mainViewModel.messageDialogState.onConfirm,
                    showDialog = mainViewModel.messageDialogState.isShown
                )
            }
        }
    }
}