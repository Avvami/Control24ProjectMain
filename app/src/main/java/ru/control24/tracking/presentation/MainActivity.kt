package ru.control24.tracking.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import ru.control24.tracking.Control24Application
import ru.control24.tracking.R
import ru.control24.tracking.presentation.navigation.root.RootNavigationGraph
import ru.control24.tracking.presentation.ui.components.CustomDialog
import ru.control24.tracking.presentation.ui.theme.Control24Theme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        viewModelFactory {
            MainViewModel(
                objectsRepository = Control24Application.appModule.objectsRepository,
                objectsDetailsRepository = Control24Application.appModule.objectsDetailsRepository
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        installSplashScreen().setKeepOnScreenCondition {
//            mainViewModel.isLoading
//        }
        setContent {
            Control24Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigationGraph(
                        navHostController = rememberNavController(),
                        startDestination = mainViewModel.startDestination,
                        mainViewModel = mainViewModel
                    )
                }
                CustomDialog(
                    iconRes = mainViewModel.messageDialogState.iconRes,
                    titleRes = mainViewModel.messageDialogState.titleRes,
                    messageRes = mainViewModel.messageDialogState.messageRes,
                    messageString = mainViewModel.messageDialogState.messageString,
                    onDismissRequest = { mainViewModel.uiEvent(UiEvent.CloseMessageDialog) },
                    confirmButton = {
                        TextButton(onClick = { mainViewModel.uiEvent(UiEvent.CloseMessageDialog) }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                    showDialog = mainViewModel.messageDialogState.isShown
                )
            }
        }
    }
}