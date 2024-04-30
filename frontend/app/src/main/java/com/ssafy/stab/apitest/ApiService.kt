package com.ssafy.stab.apitest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("posts/{id}")
    fun getOneTickers(@Path("id") id: Int): Call<TickerResponse>

    @GET("posts")
    fun getAllTickers(): Call<List<TickerResponse>>
}