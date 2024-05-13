package ru.control24.tracking.core.data.local

import androidx.room.Entity

@Entity(primaryKeys = ["username"])
data class UsersEntity(
    val username: String,
    val password: String,
    val active: Boolean = false
)