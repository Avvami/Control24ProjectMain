package ru.control24.tracking.monitoring.presentation.objects

sealed interface ObjectsUiEvent {
    data class SetCardExpanded(val objectId: Int, val expanded: Boolean): ObjectsUiEvent
}