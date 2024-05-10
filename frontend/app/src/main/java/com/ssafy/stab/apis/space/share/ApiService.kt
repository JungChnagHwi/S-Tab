package com.ssafy.stab.apis.space.share

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @GET("api/space/list")
    fun getShareSpaceList(@Header("Authorization") authorization: String): Call<List<ShareSpaceList>>

    @POST("api/space")
    fun createShareSpace(@Header("Authorization") authorization: String, @Body createShareSpaceRequest: CreateShareSpaceRequest): Call<ShareSpaceList>
}