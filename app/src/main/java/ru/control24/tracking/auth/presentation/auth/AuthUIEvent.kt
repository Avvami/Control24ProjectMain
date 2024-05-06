package ru.control24.tracking.auth.presentation.auth

sealed interface AuthUIEvent {
    data class SetLogin(val login: String): AuthUIEvent
    data class SetPassword(val password: String): AuthUIEvent
    data class SetPasswordHidden(val hidden: Boolean): AuthUIEvent
    data class ValidateInput(val login: String? = null, val password: String? = null): AuthUIEvent
}