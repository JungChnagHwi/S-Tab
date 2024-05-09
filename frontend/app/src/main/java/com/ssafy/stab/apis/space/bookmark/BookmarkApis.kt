package com.ssafy.stab.apis.space.bookmark

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getBookMarkList() {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getBookmarkList(authorizationHeader)

    call.enqueue(object: Callback<BookmarkListResponse> {
        override fun onResponse(call: Call<BookmarkListResponse>, response: Response<BookmarkListResponse>) {
            Log.d("APIResponse", response.body().toString())
        }

        override fun onFailure(call: Call<BookmarkListResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun addBookMark(id: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val addBookmarkRequest = AddBookmarkRequest(id)
    val call = apiService.addBookmark(authorizationHeader, addBookmarkRequest)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })

}

fun deleteBookMark(fileId: String) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.deleteBookmark(authorizationHeader, fileId)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}