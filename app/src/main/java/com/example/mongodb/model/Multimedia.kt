package com.example.mongodb.model

data class Multimedia (
    val id: String,
    val name: String,
    val descripcion: String,
    val duracion: String,
    val director: String,
    val cast: List<String>,
    val gender: List<String>,
    val idMedia: String,
    val company: List<String>,
    val date: String,
    val poster: String,
    val rating: Float
)