package com.example.mongodb.model


data class Usuario(
    val id: String? = null,
    val userName: String,
    val name: String,
    val biography: String? = null,
    val genres: List<Any>? = null,
    val birthDate: String,
    val joiningDate: String,
    val password: String,
    val email: String,
    val avatar: String? = null,
    var following: List<String>? = null,
    var followers: List<String>? = null,
    var like: List<String>? = null,
    var dislike: List<String>? = null
)


