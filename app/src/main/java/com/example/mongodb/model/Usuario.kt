package com.example.mongodb.model

data class Usuario(
    val id: String?,
    val userName: String,
    val name: String,
    val birthDate : String,
    val password : String,
    val email: String,
    val avatar: String
)
