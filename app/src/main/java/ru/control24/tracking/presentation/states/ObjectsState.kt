package ru.control24.tracking.presentation.states

import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.objects_details.ObjectsInfoDetailed

data class ObjectsState(
    val objectsInfo: ObjectsInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val objectsDetails: ObjectsInfoDetailed? = null
)
