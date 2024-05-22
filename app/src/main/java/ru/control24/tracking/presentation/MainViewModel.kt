package ru.control24.tracking.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.control24.tracking.R
import ru.control24.tracking.core.domain.models.UserInfo
import ru.control24.tracking.core.domain.repository.LocalRepository
import ru.control24.tracking.core.util.Resource
import ru.control24.tracking.monitoring.data.objects.mappers.toObjectsInfo
import ru.control24.tracking.monitoring.domain.objects.repository.ObjectsRepository
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.ActiveUserState
import ru.control24.tracking.presentation.states.MessageDialogState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val objectsRepository: ObjectsRepository,
    private val localRepository: LocalRepository
): ViewModel() {

    var startDestination by mutableStateOf(RootNavGraph.AUTH)
        private set

    var messageDialogState by mutableStateOf(MessageDialogState())
        private set

    var holdSplash by mutableStateOf(true)
        private set

    private val _activeUserState = MutableStateFlow(ActiveUserState())
    val activeUserState = _activeUserState.asStateFlow()

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            localRepository.getActiveUser()
                .flatMapLatest { userEntity ->
                    if (userEntity != null) {
                        localRepository.getCurrentUserObjects(userEntity.username).map { objectsList ->
                            ActiveUserState(
                                userInfo = UserInfo(username = userEntity.username, password = userEntity.password),
                                objectsList = objectsList.map { it.toObjectsInfo() }
                            )
                        }
                    } else {
                        flowOf(ActiveUserState())
                    }
                }.collect { activeUserState ->
                    _activeUserState.value = activeUserState
                    activeUserState.userInfo?.let {
                        startDestination = RootNavGraph.HOME
                    }
                    delay(100)
                    holdSplash = false
                }
        }
    }

    private fun getObjects(login: String, password: String) {
        viewModelScope.launch {
            _activeUserState.update {
                it.copy(
                    isLoading = true
                )
            }

            var key: String? = null
            var error: String? = null

            objectsRepository.getObjects(login, password).let { result ->
                when (result) {
                    is Resource.Error -> {
                        error = result.message
                        uiEvent(UiEvent.ShowMessageDialog(
                            iconRes = R.drawable.icon_running_with_errors_fill0,
                            messageString = error
                        ))
                    }
                    is Resource.Success -> {
                        key = result.data
                        startDestination = RootNavGraph.HOME
                    }
                }
            }

            _activeUserState.update {
                it.copy(
                    key = key,
                    isLoading = false,
                    error = error
                )
            }
        }
    }

    fun uiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.AuthUser -> { getObjects(event.login, event.password) }
            is UiEvent.ShowMessageDialog -> {
                messageDialogState = messageDialogState.copy(
                    isShown = true,
                    iconRes = event.iconRes,
                    titleRes = event.titleRes,
                    messageRes = event.messageRes,
                    messageString = event.messageString,
                    dismissTextRes = event.dismissTextRes,
                    onDismissRequest = { uiEvent(UiEvent.CloseMessageDialog) },
                    onDismiss = event.onDismiss,
                    confirmTextRes = event.confirmTextRes,
                    onConfirm = event.onConfirm ?: { uiEvent(UiEvent.CloseMessageDialog) }
                )
            }
            UiEvent.CloseMessageDialog -> { messageDialogState = messageDialogState.copy(isShown = false) }
            is UiEvent.SetStartDestination -> { startDestination = event.destination }
        }
    }
}