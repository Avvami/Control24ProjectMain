package ru.control24.tracking.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MessageDialogState(
    val isShown: Boolean = false,
    @DrawableRes val iconRes: Int? = null,
    @StringRes val titleRes: Int? = null,
    @StringRes val messageRes: Int? = null,
    val messageString: String? = null
)