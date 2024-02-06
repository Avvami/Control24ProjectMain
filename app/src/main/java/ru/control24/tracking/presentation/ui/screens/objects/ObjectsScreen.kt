package ru.control24.tracking.presentation.ui.screens.objects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.control24.tracking.R
import ru.control24.tracking.presentation.states.AuthState
import ru.control24.tracking.presentation.ui.components.CustomTopAppBar

@Composable
fun ObjectsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    authState: () -> AuthState
) {

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(id = R.string.tracking_objects_title)
            )
        }
    ) { innerPadding ->
        if (authState().authInfo != null) {
            Text(
                text = stringResource(id = R.string.empty_tracking_objects),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            }
        }
    }
}