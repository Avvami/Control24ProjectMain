package ru.control24.tracking.data.remote.auth

import retrofit2.http.GET
import retrofit2.http.Path

interface AuthApi {

    @GET("login2&{login}&{password}")
    suspend fun authUser(
        @Path("login") login: String,
        @Path("password") password: String
    ): AuthDto
}