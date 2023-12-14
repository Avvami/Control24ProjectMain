package ru.control24.tracking.presentation


sealed interface UIEvent {
    data class AuthUser(val login: String, val password: String): UIEvent
    data object CloseAuthDialog: UIEvent
    data object CheckUserExist: UIEvent
}