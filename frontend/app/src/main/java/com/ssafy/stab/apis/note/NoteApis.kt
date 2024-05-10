package com.ssafy.stab.apis.note

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.request.NoteRequest
import com.ssafy.stab.data.note.response.NoteResponse
import com.ssafy.stab.data.note.response.PageListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"
fun fetchPageList(
    noteId: String = "n-2cb49f1d-9d2a-4d1d-a8e2-9272f304c734",
    onResponseSuccess: (PageListResponse) -> Unit
) {
    val call = apiService.getPageList(authorizationHeader, noteId)

    call.enqueue(object : Callback<PageListResponse> {
        override fun onResponse(
            call: Call<PageListResponse>,
            response: Response<PageListResponse>
        ) {
            if (response.isSuccessful) {
                val responseBody = response.body()!!
                onResponseSuccess(responseBody)
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

fun createNewNote(noteRequest: NoteRequest) {
    val call = apiService.createNote(authorizationHeader, noteRequest)

    call.enqueue(object : Callback<NoteResponse> {
        override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
            if (response.isSuccessful) {
                val noteResponse = response.body()
                Log.i("createNewNote", noteResponse.toString())
            } else {
                Log.e("createNewNote", "${response.code()}: ${response.errorBody()?.string()}")
            }
        }


        override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
            Log.e("createNewNote", "err: ${t.localizedMessage}")
            t.printStackTrace()
        }
    })
}