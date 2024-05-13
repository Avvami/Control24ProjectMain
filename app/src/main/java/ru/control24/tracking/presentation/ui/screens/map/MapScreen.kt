package ru.control24.tracking.presentation.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import ru.control24.tracking.core.components.MapBoxMap

@Composable
fun MapScreen() {
    MapBoxMap(
        modifier = Modifier.fillMaxSize(),
        point = Point.fromLngLat(-0.6333, 35.6971)
    )
}