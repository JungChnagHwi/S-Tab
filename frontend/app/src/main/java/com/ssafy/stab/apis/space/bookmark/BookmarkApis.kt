package com.ssafy.stab.apis.space.bookmark

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"
fun getBookMarkList(onFolderResult: (List<BookmardFolder>?) -> Unit, onNoteResult: (List<BookmardNote>?) -> Unit, onPageResult: (List<BookmardPage>?) -> Unit) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val call = apiService.getBookmarkList(authorizationHeader)

    call.enqueue(object: Callback<BookmarkListResponse> {
        override fun onResponse(call: Call<BookmarkListResponse>, response: Response<BookmarkListResponse>) {
            if (response.isSuccessful) {
                Log.d("getBookMarkList", response.body().toString())
                val fileListResponse = response.body()
                onFolderResult(fileListResponse?.folders)
                onNoteResult(fileListResponse?.notes)
                onPageResult(fileListResponse?.pages)
            } else {
                println("getBookMarkList, Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<BookmarkListResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun addBookMark(id: String) {
    val addBookmarkRequest = AddBookmarkRequest(id)
    val call = apiService.addBookmark(authorizationHeader, addBookmarkRequest)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("addBookMark", "$id: $response")
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("addBookMark", "요청 실패")
        }
    })

}

fun deleteBookMark(fileId: String) {
    val call = apiService.deleteBookmark(authorizationHeader, fileId)

    call.enqueue(object: Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("deleteBookMark", "$fileId: $response")
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("deleteBookMark", "요청 실패")
        }
    })
}

fun getFolderPath(parentFolderId: String, folderId: String, onResult: (GetPathResponse) -> Unit) {
    val getPathRequest = GetPathRequest(parentFolderId, folderId)
    val call = apiService.getFolderPath(authorizationHeader, getPathRequest)

    call.enqueue(object: Callback<GetPathResponse> {
        override fun onResponse(call: Call<GetPathResponse>, response: Response<GetPathResponse>) {
            if (response.isSuccessful) {
                val res = response.body()
                if (res != null) {
                    onResult(res)
                }
            } else {
                Log.d("폴더 경로", "요청 실패")
            }
        }

        override fun onFailure(call: Call<GetPathResponse>, t: Throwable) {
            Log.d("폴더 경로", "기타 사유로 요청 실패")
        }
    })
}