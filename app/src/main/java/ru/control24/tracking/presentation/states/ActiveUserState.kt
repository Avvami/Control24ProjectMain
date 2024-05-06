package ru.control24.tracking.presentation.states

import ru.control24.tracking.monitoring.domain.objects.models.ObjectsInfo
import ru.control24.tracking.core.domain.models.UserInfo

data class ActiveUserState(
    val userInfo: UserInfo? = null,
    val objectsList: List<ObjectsInfo>? = null,
    val key: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
