package com.example.mongodb.model

data class Post(
    val id: String?,
    val user: String,
    val userId: String,

    val title: String,
    val content: String,
    val date: String,
    val time: String,
    val mediaName: String,
    val mediaId: String,
    val mediaImg: String,
    val postType: String,
    val likes: Long = 0,
    val likesInformation: List<LikeInformation>,
    var comments: List<Comentario>? = null,
)