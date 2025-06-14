package com.example.mongodb.model

data class LoginResponse (
    val success: Boolean,
    val message: String,
    val accessToken: String?,
    val refreshToken: String?,
    val userId: String?,
    val user: String?
)