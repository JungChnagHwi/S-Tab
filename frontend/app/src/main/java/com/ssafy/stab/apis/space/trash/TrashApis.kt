package com.ssafy.stab.apis.space.trash

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"

fun getTrashList(onFolderResult: (List<TrashFolder>?) -> Unit, onNoteResult: (List<TrashNote>?) -> Unit, onPageResult: (List<TrashPage>?) -> Unit) {
    val call = apiService.getTrashList(authorizationHeader)

    call.enqueue(object: Callback<GetTrashListResponse>{
        override fun onResponse(
            call: Call<GetTrashListResponse>,
            response: Response<GetTrashListResponse>
        ) {
            if (response.isSuccessful) {
                Log.d("APIResponse", response.body().toString())
                val fileListResponse = response.body()
                onFolderResult(fileListResponse?.folders)
                onNoteResult(fileListResponse?.notes)
                onPageResult(fileListResponse?.pages)
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<GetTrashListResponse>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun restoreTrash(id: String) {
    val restoreTrashRequest = RestoreTrashRequest(id)
    val call = apiService.restoreTrash(authorizationHeader, restoreTrashRequest)

    call.enqueue(object: Callback<Void>{
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "요청 성공")
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}