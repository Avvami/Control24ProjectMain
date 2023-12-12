package ru.control24.tracking.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.control24.tracking.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomAlertDialog(
    error: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = { onDismiss() }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.icon_running_with_errors_fill0), contentDescription = "Error", tint = MaterialTheme.colorScheme.primary)
                Text(text = stringResource(id = R.string.error_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(id = R.string.error_text), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = error, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            TextButton(
                onClick = { onConfirm() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.error_confirm), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}