package ru.control24.tracking.presentation.ui.screens.objects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.control24.tracking.R
import ru.control24.tracking.presentation.states.ObjectsState
import ru.control24.tracking.presentation.ui.components.CustomTopAppBar
import ru.control24.tracking.presentation.ui.components.ObjectCardCompact
import ru.control24.tracking.presentation.ui.components.ThinLinearProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectsScreen(
    paddingValues: PaddingValues,
    objectsState: () -> ObjectsState
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
            AnimatedVisibility(visible = objectsState().isLoading) {
                ThinLinearProgressIndicator()
            }
            objectsState().error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            objectsState().objectsInfo?.let { objectsInfo ->
                if (objectsInfo.trackingObjects.isEmpty()) {
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
                            count = objectsInfo.trackingObjects.size,
                            key = { objectsInfo.trackingObjects[it].id }
                        ) { index ->
                            val objectInfo = objectsInfo.trackingObjects[index]
                            val objectDetails = objectsState().objectsDetails?.objects?.associateBy { it.id }?.get(objectInfo.id)
                            ObjectCardCompact(
                                objectInfo = objectInfo,
                                objectInfoDetails = objectDetails,
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