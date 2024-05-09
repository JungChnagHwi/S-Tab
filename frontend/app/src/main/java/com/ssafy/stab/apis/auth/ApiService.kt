package com.ssafy.stab.apis.auth

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @POST("api/auth/login")
    fun getTokens(@Body idToken: IdTokenRequest): Call<TokenResponse>

    @GET("api/user")
    fun getInfoIfUser(@Header("Authorization") authorization: String): Call<AuthResponse>

    @POST("api/user")
    fun getInfoNewUser(@Header("Authorization") authorization: String, @Body signupRequest: UserSignupRequest): Call<AuthResponse>

    @GET("api/s3")
    fun getS3URI(@Header("Authorization") authorization: String, @Query("filename") filename: String): Call<String>

    @GET("api/user/{nickname}")
    fun checkNickname(@Header("Authorization") authorization: String, @Path("nickname") nickname: String): Call<NickNameResponse>
}


