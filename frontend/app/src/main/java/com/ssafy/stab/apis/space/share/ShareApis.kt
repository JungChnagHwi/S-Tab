package com.ssafy.stab.apis.space.share

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"

fun getShareSpaceList() {
    val call = apiService.getShareSpaceList(authorizationHeader)

    call.enqueue(object: Callback<List<ShareSpaceList>> {
        override fun onResponse(
            call: Call<List<ShareSpaceList>>,
            response: Response<List<ShareSpaceList>>
        ) {
            Log.d("APIResponse", response.toString())
        }

        override fun onFailure(call: Call<List<ShareSpaceList>>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun createShareSpace(title: String) {
    val createShareSpaceRequest = CreateShareSpaceRequest(title)
    val call = apiService.createShareSpace(authorizationHeader, createShareSpaceRequest)

    call.enqueue(object: Callback<ShareSpaceList> {
        override fun onResponse(call: Call<ShareSpaceList>, response: Response<ShareSpaceList>) {
            Log.d("APIResponse", response.body().toString())
        }

        override fun onFailure(call: Call<ShareSpaceList>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}