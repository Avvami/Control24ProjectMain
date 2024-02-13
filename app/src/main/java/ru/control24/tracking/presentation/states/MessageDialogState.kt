package ru.control24.tracking.presentation.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.control24.tracking.R

data class MessageDialogState(
    val isShown: Boolean = false,
    @DrawableRes val iconRes: Int? = null,
    @StringRes val titleRes: Int? = null,
    @StringRes val messageRes: Int? = null,
    val messageString: String? = null,
    val onDismissRequest: () -> Unit = {},
    @StringRes val dismissTextRes: Int? = null,
    val onDismiss: (() -> Unit)? = null,
    @StringRes val confirmTextRes: Int = R.string.ok,
    val onConfirm: () -> Unit = {}
)