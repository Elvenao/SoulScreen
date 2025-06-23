package com.example.mongodb.model

data class NuevoComentarioRequest(
    val userId: String,
    val userName: String,
    val comentario: String
)