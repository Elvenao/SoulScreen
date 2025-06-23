package com.example.mongodb.model
import com.example.mongodb.model.Post

data class PostWithAvatar(
    val post: Post,
    val userAvatar: String?
)