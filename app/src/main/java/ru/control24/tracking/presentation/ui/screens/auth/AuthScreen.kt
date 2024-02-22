package ru.control24.tracking.presentation.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.control24.tracking.R
import ru.control24.tracking.presentation.UiEvent
import ru.control24.tracking.presentation.states.ActiveUserState
import ru.control24.tracking.presentation.ui.components.CustomTopAppBar
import ru.control24.tracking.presentation.ui.theme.md_theme_light_onPrimary
import ru.control24.tracking.presentation.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    uiEvent: (UiEvent) -> Unit,
    activeUserState: ActiveUserState,
    navigateToHelpScreen: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    Scaffold(
        topBar = {
            CustomTopAppBar(
                hasHelpAction = true,
                navigateToHelpScreen = { navigateToHelpScreen() },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(color = md_theme_light_primary)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = stringResource(id = R.string.company_logo),
                    modifier = Modifier.width(260.dp),
                    tint = md_theme_light_onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.company_description),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .width(488.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = authViewModel.loginFieldState,
                onValueChange = {
                    authViewModel.authUIEvent(AuthUIEvent.SetLogin(it))
                    authViewModel.authUIEvent(AuthUIEvent.ValidateInput(login = it))
                },
                label = {
                    AnimatedContent(
                        targetState = authViewModel.loginInputError,
                        label = "Login text field error label animation",
                        transitionSpec = {
                            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) togetherWith
                                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
                        }
                    ) {loginInputError ->
                        Text(
                            text = when (loginInputError) {
                                true -> { stringResource(id = R.string.login_hint_error) }
                                false -> { stringResource(id = R.string.login_hint) }
                            }
                        )
                    }
                },
                isError = authViewModel.loginInputError,
                singleLine = true,
                modifier = Modifier
                    .width(488.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = authViewModel.passwordFieldState,
                onValueChange = {
                    authViewModel.authUIEvent(AuthUIEvent.SetPassword(it))
                    authViewModel.authUIEvent(AuthUIEvent.ValidateInput(password = it))
                },
                label = {
                    AnimatedContent(
                        targetState = authViewModel.passwordInputError,
                        label = "Password text field error label animation",
                        transitionSpec = {
                            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) togetherWith
                                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
                        }
                    ) {passwordInputError ->
                        Text(
                            text = when (passwordInputError) {
                                true -> { stringResource(id = R.string.password_hint_error) }
                                false -> { stringResource(id = R.string.password_hint) }
                            }
                        )
                    }
                },
                isError = authViewModel.passwordInputError,
                visualTransformation = if (authViewModel.passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { authViewModel.authUIEvent(AuthUIEvent.SetPasswordHidden(!authViewModel.passwordHidden)) }) {
                        val visibilityIcon = if (authViewModel.passwordHidden) painterResource(id = R.drawable.icon_visibility_fill1) else
                            painterResource(id = R.drawable.icon_visibility_off_fill1)
                        val description = if (authViewModel.passwordHidden) "Show password" else "Hide password"
                        Icon(painter = visibilityIcon, contentDescription = description)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .width(488.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    authViewModel.authUIEvent(AuthUIEvent.ValidateInput(authViewModel.loginFieldState, authViewModel.passwordFieldState))
                    if (authViewModel.loginInputError || authViewModel.passwordInputError) return@Button
                    uiEvent(UiEvent.AuthUser(authViewModel.loginFieldState, authViewModel.passwordFieldState))
                },
                modifier = Modifier
                    .width(488.dp)
                    .padding(horizontal = 16.dp)
            ) {
                AnimatedContent(targetState = activeUserState.isLoading, label = "Auth progress animation") { targetState ->
                    if (targetState) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = stringResource(id = R.string.login))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}