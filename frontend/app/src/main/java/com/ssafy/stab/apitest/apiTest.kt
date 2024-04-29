package com.ssafy.stab.apitest

import android.util.Log
import retrofit2.Call

fun apiTestWhole() {
    val apiService = RetrofitClient.getClient().create(ApiService::class.java)
    val call = apiService.getAllTickers()

    call.enqueue(object : retrofit2.Callback<List<TickerResponse>> {
        override fun onResponse(call: Call<List<TickerResponse>>, response: retrofit2.Response<List<TickerResponse>>) {
            if (response.isSuccessful && response.body() != null) {
                val tickerResponse = response.body()
                Log.i("APIResponse", "Successful response: $tickerResponse")
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }

        override fun onFailure(call: Call<List<TickerResponse>>, t: Throwable) {
            t.printStackTrace()
        }
    })

}

fun apiTestOne(id:Int) {
    val apiService = RetrofitClient.getClient().create(ApiService::class.java)
    val call = apiService.getOneTickers(id)

    call.enqueue(object : retrofit2.Callback<TickerResponse> {
        override fun onResponse(call: Call<TickerResponse>, response: retrofit2.Response<TickerResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val tickerResponse = response.body()
                Log.i("APIResponse", "Successful response: $tickerResponse")
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }

        override fun onFailure(call: Call<TickerResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })

}