package ru.control24.tracking.settings.presentation.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.control24.tracking.R
import ru.control24.tracking.core.presentation.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    popBackStack: () -> Unit
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(id = R.string.help),
                hasNavigationIcon = true,
                popBackStack = { popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Hello")
        }
    }
}