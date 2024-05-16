package com.ssafy.stab.apis.gpt

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/gpt")
    fun sendQuestion(@Header("Authorization") authorization: String, @Body gptRequest: GPTRequest): Call<GPTResponse>
}