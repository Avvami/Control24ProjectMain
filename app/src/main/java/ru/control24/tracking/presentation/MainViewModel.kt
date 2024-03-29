package ru.control24.tracking.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.control24.tracking.R
import ru.control24.tracking.data.mappers.toObjectsInfo
import ru.control24.tracking.domain.user.UserInfo
import ru.control24.tracking.domain.repository.ObjectsRepository
import ru.control24.tracking.domain.repository.UsersLocalRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.ActiveUserState
import ru.control24.tracking.presentation.states.MessageDialogState

class MainViewModel(
    private val objectsRepository: ObjectsRepository,
    private val usersLocalRepository: UsersLocalRepository
): ViewModel() {

    var startDestination by mutableStateOf(RootNavGraph.AUTH)
        private set

    var messageDialogState by mutableStateOf(MessageDialogState())
        private set

    var userCheckResult by mutableStateOf<Boolean?>(null)
        private set

    private val _activeUserState = MutableStateFlow(ActiveUserState())
    val activeUserState = _activeUserState.asStateFlow()

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            usersLocalRepository.getActiveUser()
                .flatMapLatest { userEntity ->
                    usersLocalRepository.loadCurrentUserObjects(userEntity.username).map { objectsList ->
                        ActiveUserState(
                            userInfo = UserInfo(username = userEntity.username, password = userEntity.password),
                            objectsList = objectsList.map { it.toObjectsInfo() }
                        )
                    }
                }.catch {e ->
                    e.printStackTrace()
                    if (userCheckResult == null) userCheckResult = false
                }.collect { activeUserState ->
                    _activeUserState.value = activeUserState
                    if (userCheckResult == null) {
                        startDestination = RootNavGraph.HOME
                        delay(100L)
                        userCheckResult = true
                    }
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