package com.ssafy.stab.apis.gpt

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"

fun sendQuestion(question: String, onResult: (GPTResponse) -> Unit) {
    val gptRequest = GPTRequest(question)
    val call = apiService.sendQuestion(authorizationHeader, gptRequest)

    call.enqueue(object: Callback<GPTResponse> {
        override fun onResponse(call: Call<GPTResponse>, response: Response<GPTResponse>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    answer -> 
                    onResult(answer)
                }
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<GPTResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}