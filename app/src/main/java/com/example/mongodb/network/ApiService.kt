package com.example.mongodb.network

import android.util.Log
import com.example.mongodb.model.Usuario
import com.example.mongodb.model.Post
import com.example.mongodb.model.LoginResponse
import com.example.mongodb.model.LoginRequest
import com.example.mongodb.model.TokenResponse
import com.example.mongodb.model.RefreshTokenRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: RefreshTokenRequest): Call<TokenResponse>

    @GET("users")
    fun getUsuarios(): Call<List<Usuario>>

    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @POST("signup")
    suspend fun signUp(@Body usuario: Usuario): Response<LoginResponse>

    @POST("posts")
    fun crearPost(@Body post: Post): Call<Post>

    @POST("auth/login")
    suspend fun logIn(@Body loginRequest:LoginRequest): Response<LoginResponse>

}
