package ru.control24.tracking.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.control24.tracking.R
import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.objects_details.ObjectsInfoDetailed
import ru.control24.tracking.domain.repository.ObjectsRepository
import ru.control24.tracking.domain.repository.ObjectsDetailsRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.MessageDialogState
import ru.control24.tracking.presentation.states.ObjectsState

class MainViewModel(
    private val objectsRepository: ObjectsRepository,
    private val objectsDetailsRepository: ObjectsDetailsRepository
): ViewModel() {

    var startDestination by mutableStateOf(RootNavGraph.AUTH)
        private set

    var messageDialogState by mutableStateOf(MessageDialogState())
        private set

    var objectsState by mutableStateOf(ObjectsState())
        private set

    private fun getObjects(login: String, password: String) {
        viewModelScope.launch {
            objectsState = objectsState.copy(
                isLoading = true,
                error = null
            )

            var objectsInfo: ObjectsInfo? = null
            var error: String? = null

            objectsRepository.getObjects(login, password).let { result ->
                when (result) {
                    is Resource.Error -> {
                        error = result.message
                        uiEvent(UiEvent.ShowMessageDialog(
                            iconRes = R.drawable.icon_running_with_errors_fill0,
                            messageRes = R.string.http_400
                        ))
                    }
                    is Resource.Success -> {
                        objectsInfo = result.data
                        startDestination = RootNavGraph.HOME
                        getObjectsDetails(result.data!!.key)
                    }
                }
            }

            objectsState = objectsState.copy(
                objectsInfo = objectsInfo,
                isLoading = false,
                error = error
            )
        }
    }

    private fun getObjectsDetails(key: String) {
        viewModelScope.launch {
            objectsDetailsRepository.getObjectsDetails(key = key).let { result ->
                var objectDetails: ObjectsInfoDetailed? = null
                when (result) {
                    is Resource.Error -> {
                        println(result.message)
                    }
                    is Resource.Success -> {
                        objectDetails = result.data
                    }
                }
                objectsState = objectsState.copy(
                    objectsDetails = objectDetails
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
                    messageString = event.messageString
                )
            }
            UiEvent.CloseMessageDialog -> { messageDialogState = MessageDialogState() }
        }
    }
}