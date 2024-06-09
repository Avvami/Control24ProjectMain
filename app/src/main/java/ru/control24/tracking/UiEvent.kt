package ru.control24.tracking

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yandex.mapkit.mapview.MapView


sealed interface UiEvent {
    data class AuthUser(val login: String, val password: String): UiEvent
    data class ShowMessageDialog(
        @DrawableRes val iconRes: Int? = null,
        @StringRes val titleRes: Int? = null,
        @StringRes val messageRes: Int? = null,
        val messageString: String? = null,
        @StringRes val dismissTextRes: Int? = null,
        val onDismiss: (() -> Unit)? = null,
        @StringRes val confirmTextRes: Int = R.string.ok,
        val onConfirm: (() -> Unit)? = null
    ): UiEvent
    data object CloseMessageDialog: UiEvent
    data class SetStartDestination(val destination: String): UiEvent
    data class SetMapView(val mapView: MapView): UiEvent
}