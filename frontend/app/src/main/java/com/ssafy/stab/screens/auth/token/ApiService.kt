package com.ssafy.stab.screens.auth.token

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST



interface ApiService {

    data class IdTokenRequest(
        @SerializedName("idToken")
        val idToken: String
    )

    @POST("api/auth/login")
    fun getTokens(@Body idToken: IdTokenRequest): Call<TokenResponse>

    @GET("api/user/login")
    fun getInfoIfUser(@Header("Authorization") authorization: String): Call<AuthResponse>

    @POST("api/user/signup")
    fun getInfoNewUser(@Header("Authorization") authorization: String, @Body signupRequest: UserSignupRequest): Call<AuthResponse>
}