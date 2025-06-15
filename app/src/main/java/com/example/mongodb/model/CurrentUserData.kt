package com.example.mongodb.model

data class CurrentUserData (
    val id: String,
    val userName: String,
    val name: String,
    val birthDate: String,
    val biography: String,
    val genres: List<String>,
    val avatar: String
)