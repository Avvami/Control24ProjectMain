package ru.control24.tracking.presentation.ui.screens.objects

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.control24.tracking.presentation.states.AuthState

@Composable
fun ObjectsScreen(
    navController: NavController,
    authState: () -> AuthState
) {

    if (authState().authInfo == null) {
        Text(
            text = "Empty here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                count = authState().authInfo!!.trackingObjects.size,
                key = { authState().authInfo!!.trackingObjects[it].id }
            ) { index ->
                val trackingObject = authState().authInfo!!.trackingObjects[index]
                Text(text = trackingObject.name)
            }
        }
    }
}