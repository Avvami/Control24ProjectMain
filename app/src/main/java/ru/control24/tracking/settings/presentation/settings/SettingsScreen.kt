package ru.control24.tracking.settings.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.control24.tracking.R
import ru.control24.tracking.core.presentation.ActiveUserState
import ru.control24.tracking.core.presentation.components.CustomTopAppBar
import ru.control24.tracking.settings.presentation.settings.components.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    navigateToHelpScreen: () -> Unit,
    activeUserState: State<ActiveUserState>
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            CustomTopAppBar(
                title = stringResource(id = R.string.settings)
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0.dp),
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Profile(activeUserState = activeUserState)
            }
            item {
                Column {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(id = R.string.general_settings),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /*TODO: Navigate to map settings*/ }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_map_fill0),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.map_settings),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = .5.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /*TODO: Navigate to appearance*/ }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_palette_fill0),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.appearance),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            item {
                Column {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(id = R.string.support_settings),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /*TODO: Navigate to about app*/ }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_info_fill0),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.about_app),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = .5.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigateToHelpScreen() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_help_fill0),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.help),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}