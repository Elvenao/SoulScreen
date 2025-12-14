package com.example.mongodb.model

data class UpdateProfileRequest (
    val userName: String,
    val name: String,
    val biography: String,
    val genres: List<Any>,
    val birthDate: String,
)