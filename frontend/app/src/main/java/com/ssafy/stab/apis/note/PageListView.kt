package com.ssafy.stab.apis.note

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.response.PageListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PageListView : ViewModel() {
    private val _pageList = MutableLiveData<PageListResponse?>()
    val pageList: LiveData<PageListResponse?> = _pageList

    fun fetchPageList(noteId: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val accessToken = PreferencesUtil.getLoginDetails().accessToken
        val authorizationHeader = "Bearer $accessToken"
        val call = apiService.getPageList(authorizationHeader, noteId)

        call.enqueue(object : Callback<PageListResponse> {
            override fun onResponse(
                call: Call<PageListResponse>,
                response: Response<PageListResponse>
            ) {
                if (response.isSuccessful) {
                    _pageList.value = response.body()
                    Log.i("fetchPageList", "isSuccessful")
                } else {
                    Log.e("fetchPageList", "${response.code()}: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PageListResponse>, t: Throwable) {
                Log.e("fetchPageList", "err: ${t.localizedMessage}")
            }
        })
    }
}