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



fun createNewNote(noteRequest: NoteRequest) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
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