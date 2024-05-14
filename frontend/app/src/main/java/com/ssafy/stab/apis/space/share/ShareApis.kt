package com.ssafy.stab.apis.space.share

import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.apis.space.folder.FileListResponse
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.data.PreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"

fun getFileListShareSpace(spaceId: String, onFolderResult: (List<Folder>?) -> Unit, onNoteResult: (List<Note>?) -> Unit) {
    val call = apiService.getFileListShareSpace(authorizationHeader, spaceId)

    call.enqueue(object : Callback<FileListResponse> {
        override fun onResponse(call: Call<FileListResponse>, response: Response<FileListResponse>) {
            if (response.isSuccessful) {
                val fileListResponse = response.body()
                onFolderResult(fileListResponse?.folders)
                onNoteResult(fileListResponse?.notes)
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }
        override fun onFailure(call: Call<FileListResponse>, t: Throwable) {
            println("Error fetching file list: ${t.message}")

        }
    })
}

fun getShareSpace(spaceId: String, onResult: (ShareSpace) -> Unit) {
    val call = apiService.getShareSpace(authorizationHeader, spaceId)

    call.enqueue(object : Callback<ShareSpace> {
        override fun onResponse(call: Call<ShareSpace>, response: Response<ShareSpace>) {
            if (response.isSuccessful) {
                Log.d("APIResponse", response.body().toString())
                response.body()?.let { onResult(it) }
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(p0: Call<ShareSpace>, p1: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun getShareSpaceList(onResult: (List<ShareSpaceList>) -> Unit) {
    val call = apiService.getShareSpaceList(authorizationHeader)

    call.enqueue(object: Callback<List<ShareSpaceList>> {
        override fun onResponse(
            call: Call<List<ShareSpaceList>>,
            response: Response<List<ShareSpaceList>>
        ) {
            if (response.isSuccessful) {
                Log.d("APIResponse", response.body().toString())
                response.body()?.let { onResult(it) }
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<List<ShareSpaceList>>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun createShareSpace(title: String, onResult: (ShareSpaceList) -> Unit) {
    val createShareSpaceRequest = CreateShareSpaceRequest(title)
    val call = apiService.createShareSpace(authorizationHeader, createShareSpaceRequest)

    call.enqueue(object: Callback<ShareSpaceList> {
        override fun onResponse(call: Call<ShareSpaceList>, response: Response<ShareSpaceList>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    shareSpace -> Log.d("APIResponse", shareSpace.toString())
                    onResult(shareSpace)
                }
            } else {
                println("Response not successful: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<ShareSpaceList>, t: Throwable) {
            Log.d("APIResponse", "요청 실패")
        }
    })
}

fun participateShareSpace(spaceId: String) {
    val participateShareSpaceRequest = ParticipateShareSpaceRequest(spaceId)
    val call = apiService.participateShareSpace(authorizationHeader, participateShareSpaceRequest)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "참가 성공")
            } else {
                Log.d("APIResponse", "요청 실패")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "기타 오류로 요청 실패")
        }
    })
}

fun leaveShareSpace(spaceId: String) {
    val call = apiService.leaveShareSpace(authorizationHeader, spaceId)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "나가기 성공")
            } else {
                Log.d("APIResponse", "요청 실패")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "기타 이유로 요청 실패")
        }
    })
}

fun renameShareSpace(spaceId: String, title: String) {
    val renameShareSpaceRequest = RenameShareSpaceRequest(spaceId, title)
    val call = apiService.renameShareSpace(authorizationHeader,renameShareSpaceRequest)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "이름 변경")
            } else {
                Log.d("APIResponse", "요청 실패")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "기타 이유로 요청 실패")
        }
    })
}

fun getMarkdown(spaceId: String, onResult: (MarkdownDataResponse) -> Unit) {
    val call = apiService.getMarkDown(authorizationHeader, spaceId)

    call.enqueue(object: Callback<MarkdownDataResponse> {
        override fun onResponse(
            call: Call<MarkdownDataResponse>,
            response: Response<MarkdownDataResponse>
        ) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "요청 성공")
                response.body()?.let { onResult(it) }
            } else {
                Log.d("APIResponse", "요청 실패")
                onResult(MarkdownDataResponse(""))
            }
        }

        override fun onFailure(call: Call<MarkdownDataResponse>, t: Throwable) {
            Log.d("APIResponse", "기타 이유로 요청 실패")
        }
    })
}

fun patchMarkdown(spaceId: String, data: String) {
    val patchRequest = MarkdownDataPatchRequest(spaceId, data)
    val call = apiService.patchMarkDown(authorizationHeader, patchRequest)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("APIResponse", "수정 완료")
            } else {
                Log.d("APIRespones", "수정 실패")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.d("APIResponse", "기타 이유로 요청 실패")
        }
    })
}