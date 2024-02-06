package ru.control24.tracking.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThinLinearProgressIndicator() {
    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
}