package com.example.mongodb.model

data class Post(
    val id: String?,
    val user: String,
    val userId: String,
    val title: String,
    val content: String,
    val date: String,
    val time: String
)