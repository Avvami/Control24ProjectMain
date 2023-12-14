package ru.control24.tracking.presentation.states

import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.objects.ObjectsInfoDetailed

data class AuthState(
    val authInfo: AuthInfo? = null,
    val authInProcess: Boolean = false,
    val authError: String? = null,
    val showAuthDialog: Boolean = false,
    val objectsDetails: ObjectsInfoDetailed? = null
)
