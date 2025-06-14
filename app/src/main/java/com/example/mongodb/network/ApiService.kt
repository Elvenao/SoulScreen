package com.example.mongodb.network

import com.example.mongodb.model.Usuario
import com.example.mongodb.model.Post
import com.example.mongodb.model.LoginResponse
import com.example.mongodb.model.LoginRequest
import com.example.mongodb.model.TokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<TokenResponse>

    @GET("users")
    fun getUsuarios(): Call<List<Usuario>>

    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @POST("users")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @POST("posts")
    fun crearPost(@Body post: Post): Call<Post>

    @POST("auth/login")
    suspend fun logIn(@Body loginRequest:LoginRequest): Response<LoginResponse>

}
