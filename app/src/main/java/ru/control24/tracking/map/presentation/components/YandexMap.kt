package ru.control24.tracking.map.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.mapview.MapView
import ru.control24.tracking.core.util.findActivity
import ru.control24.tracking.MainActivity
import ru.control24.tracking.UiEvent

@Composable
fun YandexMap(
    modifier: Modifier = Modifier,
    uiEvent: (UiEvent) -> Unit
) {
    val activity = LocalContext.current.findActivity() as MainActivity
    DisposableEffect(key1 = Unit) {
        onDispose {
            activity.onMapStop()
        }
    }
    AndroidView(
        factory = { context ->
            MapView(context).also {
                uiEvent(UiEvent.SetMapView(it))
                activity.onMapStart()
            }
        },
        modifier = modifier.fillMaxSize(),
        update = {}
    )
}