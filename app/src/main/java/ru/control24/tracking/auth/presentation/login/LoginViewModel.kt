package ru.control24.tracking.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    var loginFieldState by mutableStateOf("")
        private set
    var passwordFieldState by mutableStateOf("")
        private set

    var passwordHidden by mutableStateOf(true)
        private set

    var loginInputError by mutableStateOf(false)
        private set

    var passwordInputError by mutableStateOf(false)
        private set

    private fun validateInput(login: String?, password: String?) {
        if (login?.isEmpty() == true) {
            loginInputError = true
            return
        }
        if (login?.isNotEmpty() == true) {
            loginInputError = false
        }

        if (password?.isEmpty() == true) {
            passwordInputError = true
            return
        }
        if (password?.isNotEmpty() == true) {
            passwordInputError = false
        }
    }

    fun authUIEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.SetLogin -> { loginFieldState = event.login }
            is LoginUIEvent.SetPassword -> { passwordFieldState = event.password }
            is LoginUIEvent.SetPasswordHidden -> { passwordHidden = event.hidden }
            is LoginUIEvent.ValidateInput -> { validateInput(event.login, event.password) }
        }
    }
}