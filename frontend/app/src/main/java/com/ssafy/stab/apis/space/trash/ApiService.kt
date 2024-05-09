package com.ssafy.stab.apis.space.trash

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface ApiService {

    @GET("api/trash")
    fun getTrashList(@Header("Authorization") authorization: String): Call<GetTrashListResponse>

    @PATCH("api/trash")
    fun restoreTrash(@Header("Authorization") authorization: String, restoreTrashRequest: RestoreTrashRequest): Call<Void>
}