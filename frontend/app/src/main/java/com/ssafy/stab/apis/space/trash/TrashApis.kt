package com.ssafy.stab.apis.space.trash

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getTrashList() {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getTrashList(authorizationHeader)

    call.enqueue(object: Callback<GetTrashListResponse>{
        override fun onResponse(
            call: Call<GetTrashListResponse>,
            response: Response<GetTrashListResponse>
        ) {
            Log.d("APIResponse", response.body().toString())
        }

        override fun onFailure(call: Call<GetTrashListResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun restoreTrash(id: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val restoreTrashRequest = RestoreTrashRequest(id)
    val call = apiService.restoreTrash(authorizationHeader, restoreTrashRequest)

    call.enqueue(object: Callback<Void>{
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}