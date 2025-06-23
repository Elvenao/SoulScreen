package com.example.mongodb.network

import android.util.Log
import com.example.mongodb.model.Category
import com.example.mongodb.model.Usuario
import com.example.mongodb.model.Post
import com.example.mongodb.model.LoginResponse
import com.example.mongodb.model.LoginRequest
import com.example.mongodb.model.PostCreated
import com.example.mongodb.model.PostRequest
import com.example.mongodb.model.TokenResponse
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.model.UpdateCategoriesRequest
import com.example.mongodb.model.UserNameRequest
import okhttp3.MultipartBody
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

    @POST("signup/userName")
    suspend fun repeteadUserName(@Body userName: UserNameRequest): Response<LoginResponse>

    @POST("posts/create")
    suspend fun crearPost(@Body post: Post): Response<PostCreated>

    @POST("posts/details")
    suspend fun verPost(@Body postRequest: PostRequest): Call<Post>

    @PATCH("users/categories/{id}")
    suspend fun updateCategories(
        @Path("id") id: String,
        @Body body: UpdateCategoriesRequest
    ): Response<LoginResponse>

    @Multipart
    @PATCH("users/avatar/{id}")
    suspend fun updateAvatar(
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<LoginResponse>

    @POST("auth/login")
    suspend fun logIn(@Body loginRequest:LoginRequest): Response<LoginResponse>

    @GET("categories")
    fun getCategories():Call<List<Category>>

}
