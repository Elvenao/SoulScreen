package com.example.mongodb.network

import com.example.mongodb.model.Usuario
import com.example.mongodb.model.Post
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("users")
    fun getUsuarios(): Call<List<Usuario>>

    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @POST("users")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @POST("posts")
    fun crearPost(@Body post: Post): Call<Post>

}
