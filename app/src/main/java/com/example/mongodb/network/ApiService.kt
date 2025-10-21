package com.example.mongodb.network

import com.example.mongodb.model.Category
import com.example.mongodb.model.LikeInformation
import com.example.mongodb.model.LoginRequest
import com.example.mongodb.model.LoginResponse
import com.example.mongodb.model.Multimedia
import com.example.mongodb.model.MultimediaIdImg
import com.example.mongodb.model.NuevoComentarioRequest
import com.example.mongodb.model.Post
import com.example.mongodb.model.PostCreated
import com.example.mongodb.model.PostWithAvatar
import com.example.mongodb.model.RefreshTokenRequest
import com.example.mongodb.model.TokenResponse
import com.example.mongodb.model.UpdateCategoriesRequest
import com.example.mongodb.model.UserData
import com.example.mongodb.model.UserIdImg
import com.example.mongodb.model.UserNameRequest
import com.example.mongodb.model.Usuario
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


data class LikeInformationUptaded(
    val message: String,
    val likes: Long,
    val likesList: List<LikeInformation>
)
interface ApiService {
    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: RefreshTokenRequest): Call<TokenResponse>
    
    @GET("users")
    fun getUsuarios(): Call<List<Usuario>>

    @GET("posts")
    fun getPosts(): Call<List<PostWithAvatar>>

    @POST("signup")
    suspend fun signUp(@Body usuario: Usuario): Response<LoginResponse>

    @POST("signup/userName")
    suspend fun repeteadUserName(@Body userName: UserNameRequest): Response<LoginResponse>

    @POST("posts/create")
    suspend fun crearPost(@Body post: Post): Response<PostCreated>

    @GET("posts/details")
    fun verPost(@Query("id") id: String): Call<PostWithAvatar>

    @POST("posts/{id}/comment")
    suspend fun commentPost(
        @Path("id") id: String,
        @Body comentario: NuevoComentarioRequest
    ): Response<PostWithAvatar>

    @POST("posts/like/{id}")
    suspend fun likePost(
        @Path("id") id: String?,
        @Body likeInformation: LikeInformation,
    ): Response<LikeInformationUptaded>
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

    @GET("multimedia/buscar")
    suspend fun getMoviesBusqueda(@Query("q") texto : String):Response<List<Multimedia>>

    @GET("users/buscar")
    suspend fun getUsuariosBusqueda(@Query("q") texto : String):Response<List<Usuario>>

    @GET("users/{id}")
    fun getUserProfile(@Path("id") id: String): Call<UserData>

    @POST("users/follow/toggle")
    fun toggleFollow(                          
        @Query("target") target: String,
        @Query("follower") follower: String
    ):Call<Void>

    @GET("users/username/{id}")
    fun getUserIdImgById(@Path("id") id: String): Call<UserIdImg>

    @GET("multimedia/idimg")
    fun getNameAndImgById(@Query("id") id: String): Call<MultimediaIdImg>

    @GET("multimedia/{id}")
    fun getMultimediaDetails(@Path("id") id: String): Call<List<Multimedia>>

    @GET("users/{idUser}/posts")
    fun getUserPosts(@Path("idUser")id:String):Call<List<Post>>

    @PATCH("users/like/{id}")
    suspend fun setLikePost(
      @Path("id") id: String,
      @Query("mediaId") mediaId: String
    ): Response<LoginResponse>

    @PATCH("users/dislike/{id}")
    suspend fun setDislikePost(
      @Path("id") id: String,
      @Query("mediaId") mediaId: String
    ): Response<LoginResponse>


    @PATCH("users/email/{id}")
    suspend fun updateEmail(
      @Path("id") id: String,
      @Query("newEmail") newEmail: String
    ): Response<LoginResponse>

    @PATCH("users/password/{id}")
    suspend fun updatePassword(
      @Path("id") id: String,
      @Query("newPassword") newPassword: String
    ): Response<LoginResponse>

    @PATCH("users/updateusername/{id}")
    suspend fun updateUsername(
      @Path("id") id: String,
      @Query("newUsername") newUsername: String
    ): Response<LoginResponse>

}
