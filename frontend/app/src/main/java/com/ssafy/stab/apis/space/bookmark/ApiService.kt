package com.ssafy.stab.apis.space.bookmark

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/like")
    fun getBookmarkList(@Header("Authorization") authorization: String): Call<BookmarkListResponse>

    @POST("api/like")
    fun addBookmark(@Header("Authorization") authorization: String, @Body addBookmarkRequest: AddBookmarkRequest): Call<Void>

    @DELETE("api/like/{fileId}")
    fun deleteBookmark(@Header("Authorization") authorization: String, @Path("fileId") fileId: String): Call<Void>
}