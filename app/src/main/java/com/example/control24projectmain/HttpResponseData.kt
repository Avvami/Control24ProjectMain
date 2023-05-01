package com.example.control24projectmain

import java.io.Serializable

data class FirstResponse (
    val key: String,
    val objects: List<FirstResponseObject>
) : Serializable

data class FirstResponseObject (
    val id: Int,
    val name: String,
    val category: String,
    val client: String,
    val avto_no: String,
    val avto_model: String,
    var is_expanded: Boolean // Maybe this need to be deleted later
) : Serializable

data class SecondResponse (
    val objects: List<SecondResponseObject>
) : Serializable

data class SecondResponseObject (
    val id: Int,
    val gmt: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
) : Serializable

data class CombinedResponse (
    val objects: List<CombinedResponseObject>
) : Serializable

data class CombinedResponseObject (
    val id: Int,
    val name: String,
    val category: Int,
    val client: String,
    val avto_no: String,
    val avto_model: String,
    val gmt: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
) : Serializable
