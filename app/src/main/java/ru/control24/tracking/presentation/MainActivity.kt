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
import ru.control24.tracking.Control24Application
import ru.control24.tracking.presentation.navigation.root.RootNavigationGraph
import ru.control24.tracking.presentation.ui.theme.Control24Theme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        viewModelFactory {
            MainViewModel(
                dataStoreRepository = Control24Application.appModule.dataStoreRepository,
                authRepository = Control24Application.appModule.authRepository,
                objectsDetailsRepository = Control24Application.appModule.objectsDetailsRepository
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading
        }
        setContent {
            Control24Theme {
                viewModel.uiEvent(UIEvent.CheckUserExist)
                println("Check in the main activity")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigationGraph(
                        navHostController = rememberNavController(),
                        startDestination = viewModel.startDestination,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}