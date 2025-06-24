package com.example.mongodb.model

data class CurrentUserData (
    val id: String,
    val userName: String,
    val name: String,
    val birthDate: String,
    val biography: String? = null,
    val genres: List<String>? = null,
    val avatar: String,
    val ip: String? = null,
    val joiningDate: String,
    var following: List<String>? = null,
    var followers: List<String>? = null
)