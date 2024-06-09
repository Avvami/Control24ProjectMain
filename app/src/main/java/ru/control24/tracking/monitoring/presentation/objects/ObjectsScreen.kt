package ru.control24.tracking.monitoring.presentation.objects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.control24.tracking.R
import ru.control24.tracking.core.presentation.ActiveUserState
import ru.control24.tracking.core.presentation.components.CustomTopAppBar
import ru.control24.tracking.core.presentation.components.ThinLinearProgressIndicator
import ru.control24.tracking.monitoring.presentation.objects.components.ObjectCardCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectsScreen(
    paddingValues: PaddingValues,
    activeUserState: ActiveUserState
) {
    val objectsViewModel: ObjectsViewModel = viewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CustomTopAppBar(
                title = stringResource(id = R.string.tracking_objects_title),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            AnimatedVisibility(visible = activeUserState.isLoading) {
                ThinLinearProgressIndicator()
            }
            activeUserState.error?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            activeUserState.objectsList?.let { objectsList ->
                if (objectsList.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.empty_tracking_objects),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            count = objectsList.size,
                            key = { objectsList[it].objectId }
                        ) { index ->
                            val objectInfo = objectsList[index]
                            ObjectCardCompact(
                                objectInfo = objectInfo,
                                isExpanded = objectsViewModel::isCardExpanded,
                                objectsUiEvent = objectsViewModel::objectsUiEvent
                            )
                        }
                    }
                }
            }
        }
    }
}