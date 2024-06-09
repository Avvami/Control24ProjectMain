package ru.control24.tracking.monitoring.presentation.objects

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class ObjectsViewModel: ViewModel() {

    var isCardExpanded = mutableStateMapOf<Int, Boolean>()
        private set

    fun objectsUiEvent(event: ObjectsUiEvent) {
        when (event) {
            is ObjectsUiEvent.SetCardExpanded -> { isCardExpanded[event.objectId] = event.expanded }
        }
    }
}