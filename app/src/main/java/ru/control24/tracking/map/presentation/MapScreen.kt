package ru.control24.tracking.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.control24.tracking.R
import ru.control24.tracking.map.presentation.components.YandexMap
import ru.control24.tracking.UiEvent
import ru.control24.tracking.core.util.ApplySystemBarsTheme
import ru.control24.tracking.ui.theme.onPrimaryLight
import ru.control24.tracking.ui.theme.onSurfaceLight
import ru.control24.tracking.ui.theme.primaryLight

@Composable
fun MapScreen(
    paddingValues: PaddingValues,
    uiEvent: (UiEvent) -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        ApplySystemBarsTheme(view = view, context = context, applyLightStatusBars = true)
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            ApplySystemBarsTheme(view = view, context = context, applyLightStatusBars = false)
        }
    }
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            YandexMap(
                uiEvent = uiEvent
            )
            Box(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(primaryLight)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = stringResource(id = R.string.company_logo),
                    tint = onPrimaryLight,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 6.dp, end = 16.dp, bottom = 6.dp)
                        .height(28.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(onSurfaceLight.copy(alpha = .2f))
                .statusBarsPadding()
        )
    }
}