package com.example.mongodb.model

data class UserData(
    val id: String,
    val name: String,
    val userName: String,
    val avatar: String,
    val biography: String?,
    val genres: List<String>?,
    val birthDate: String,
    val joiningDate: String,
    var following: List<String>? = null,
    var followers: List<String>? = null,
    var like: List<String>? = null,
    var dislike: List<String>? = null
)
