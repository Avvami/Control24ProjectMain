package ru.control24.tracking.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.datastore.DataStoreRepository
import ru.control24.tracking.domain.repository.AuthRepository
import ru.control24.tracking.domain.repository.ObjectsDetailsRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.AuthState

class MainViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository,
    private val objectsDetailsRepository: ObjectsDetailsRepository
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

            authRepository.auth(login, password).let { result ->
                when (result) {
                    is Resource.Error -> {
                        authError = result.message
                    }
                    is Resource.Success -> {
                        authInfo = result.data
                        println(result.data)
                        dataStoreRepository.saveUser(login, password)
                        getObjectsDetails(result.data!!.key)
                    }
                }
            }

            authState = authState.copy(
                authInfo = authInfo,
                authInProcess = false,
                authError = authError,
                showAuthDialog = !authError.isNullOrEmpty()
            )
        }
    }

    private fun getObjectsDetails(key: String) {
        viewModelScope.launch {
            objectsDetailsRepository.getObjectsDetails(key = key).let { result ->
                when (result) {
                    is Resource.Error -> {
                        println(result.message)
                    }
                    is Resource.Success -> {
                        println(result.data)
                    }
                }
            }
        }
    }

    private fun checkUserExist() {
        viewModelScope.launch {
            dataStoreRepository.readUserPreference.collect { user ->
                if (user.login.isNotEmpty() && user.password.isNotEmpty()) {
                    startDestination = RootNavGraph.HOME
                } else {
                    startDestination = RootNavGraph.AUTH
                }
            }
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
            UIEvent.CheckUserExist -> { checkUserExist() }
        }
    }
}