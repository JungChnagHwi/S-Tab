package com.ssafy.stab.apis.space.note

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"

fun createNote(parentFolderId: String, title: String, color: BackgroundColor, template: TemplateType, direction: Int, onResult: (CreateNoteResponse) -> Unit) {
    val createNoteRequest = CreateNoteRequest(parentFolderId, title, color, template, direction)
    val call = apiService.createNote(authorizationHeader, createNoteRequest)

    call.enqueue(object: Callback<CreateNoteResponse> {
        override fun onResponse(call: Call<CreateNoteResponse>, response: Response<CreateNoteResponse>) {
            Log.d("APIResponse", response.body().toString())
            response.body()?.let { onResult(it) }
        }

        override fun onFailure(call: Call<CreateNoteResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun createPdfNote(parentFolderId: String, pdfUrl: String, pdfPageCount: Int, title: String) {
    val createPdfNoteRequest = CreatePdfNoteRequest(parentFolderId, pdfUrl, pdfPageCount, title)
    val call = apiService.createPdfNote(authorizationHeader,createPdfNoteRequest)

    call.enqueue(object: Callback<CreateNoteResponse> {
        override fun onResponse(call: Call<CreateNoteResponse>, response: Response<CreateNoteResponse>) {
            Log.d("APIResponse", response.body().toString())
        }

        override fun onFailure(call: Call<CreateNoteResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun copyNote(noteId: String, parentFolderId: String, title: String) {
    val copyNoteRequest = CopyNoteRequest(noteId, parentFolderId, title)
    val call = apiService.copyNote(authorizationHeader, copyNoteRequest)

    call.enqueue(object: Callback<CopyNoteResponse> {
        override fun onResponse(call: Call<CopyNoteResponse>, response: Response<CopyNoteResponse>) {
            Log.d("APIResponse", response.body().toString())
        }

        override fun onFailure(call: Call<CopyNoteResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun renameNote(noteId: String, title: String) {
    val renameNoteRequest = RenameNoteRequest(noteId, title)
    val call = apiService.renameNote(authorizationHeader, renameNoteRequest)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun relocateNote(noteId: String, parentFolderId: String) {
    val relocateRequest = RelocateRequest(noteId, parentFolderId)
    val call = apiService.relocateNote(authorizationHeader, relocateRequest)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun deleteNote(noteId: String) {
    val call = apiService.deleteNote(authorizationHeader, noteId)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            Log.d("APIResponse", "요청 성공")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}