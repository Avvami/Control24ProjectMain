package ru.control24.tracking.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


sealed interface UiEvent {
    data class AuthUser(val login: String, val password: String): UiEvent
    data class ShowMessageDialog(@DrawableRes val iconRes: Int? = null, @StringRes val titleRes: Int? = null, @StringRes val messageRes: Int? = null, val messageString: String? = null): UiEvent
    data object CloseMessageDialog: UiEvent
}