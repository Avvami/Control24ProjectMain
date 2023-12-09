package ru.control24.tracking.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.control24.tracking.Control24Application
import ru.control24.tracking.presentation.ui.screens.auth.AuthScreen
import ru.control24.tracking.presentation.ui.theme.Control24Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Control24Theme {
                val viewModel = viewModel<MainViewModel>(
                    factory = viewModelFactory {
                        MainViewModel(Control24Application.appModule.authRepository)
                    }
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                ) {
                    AuthScreen(
                        uiEvent = viewModel::uiEvent,
                        authState = viewModel.authState
                    )
                }
            }
        }
    }
}