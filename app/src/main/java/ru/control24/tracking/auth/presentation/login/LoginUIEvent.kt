package ru.control24.tracking.auth.presentation.login

sealed interface LoginUIEvent {
    data class SetLogin(val login: String): LoginUIEvent
    data class SetPassword(val password: String): LoginUIEvent
    data class SetPasswordHidden(val hidden: Boolean): LoginUIEvent
    data class ValidateInput(val login: String? = null, val password: String? = null): LoginUIEvent
}