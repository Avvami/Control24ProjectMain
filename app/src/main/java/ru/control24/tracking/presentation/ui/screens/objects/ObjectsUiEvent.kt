package ru.control24.tracking.presentation.ui.screens.objects

sealed interface ObjectsUiEvent {
    data class SetCardExpanded(val objectId: Int, val expanded: Boolean): ObjectsUiEvent
}