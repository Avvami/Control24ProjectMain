package ru.control24.tracking.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.repository.AuthRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.AuthState

class MainViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    var startDestination by mutableStateOf(RootNavGraph.AUTH)
        private set

    var authState by mutableStateOf(AuthState())
        private set

    private fun authorizeUser(login: String, password: String) {
        viewModelScope.launch {
            authState = authState.copy(
                authInProcess = true,
                authError = null
            )

            var authInfo: AuthInfo? = null
            var authError: String? = null
            var showAuthDialog = false

            authRepository.auth(login, password).let { result ->
                when (result) {
                    is Resource.Error -> {
                        authError = result.message
                        showAuthDialog = true
                    }
                    is Resource.Success -> {
                        authInfo = result.data
                        startDestination = RootNavGraph.HOME
                    }
                }
            }

            authState = authState.copy(
                authInfo = authInfo,
                authInProcess = false,
                authError = authError,
                showAuthDialog = showAuthDialog
            )
        }
    }

    fun uiEvent(event: UIEvent) {
        when (event) {
            is UIEvent.AuthUser -> {
                authorizeUser(event.login, event.password)
            }
            UIEvent.CloseAuthDialog -> {
                authState = authState.copy(
                    showAuthDialog = false
                )
            }
        }
    }
}