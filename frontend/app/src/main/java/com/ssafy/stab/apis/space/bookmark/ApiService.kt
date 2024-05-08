package com.ssafy.stab.apis.space.bookmark

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("api/like")
    fun getBookmarkList(@Header("Authorization") authorization: String): Call<BookmarkListResponse>
}