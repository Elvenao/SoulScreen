package com.example.mongodb.model

data class Comentario(
    val userId: String,
    val userName: String,
    val userMedia: String,
    val comentario: String,
    val fecha: String? = null,
)