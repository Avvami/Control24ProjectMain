package ru.control24.tracking.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.control24.tracking.R
import ru.control24.tracking.presentation.ui.theme.onPrimaryLight
import ru.control24.tracking.presentation.ui.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String = "",
    hasNavigationIcon: Boolean = false,
    hasHelpAction: Boolean = false,
    navigateToHelpScreen: () -> Unit = {},
    popBackStack: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            if (title.isNotEmpty())
                Text(text = title)
        },
        navigationIcon = {
            if (hasNavigationIcon) {
                IconButton(onClick = { popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        },
        actions = {
            if (hasHelpAction) {
                IconButton(onClick = { navigateToHelpScreen() }) {
                    Icon(painter = painterResource(id = R.drawable.icon_help_fill0), contentDescription = stringResource(id = R.string.help))
                }
            } else {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(primaryLight),
                    contentAlignment = Alignment.Center
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
        },
        scrollBehavior = scrollBehavior
    )
}