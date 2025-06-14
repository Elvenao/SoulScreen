package com.example.mongodb.model

data class LoginResponse (
    val success: Boolean,
    val message: String,
    val token: String?,
    val userId: String?,
    val user: String?
)