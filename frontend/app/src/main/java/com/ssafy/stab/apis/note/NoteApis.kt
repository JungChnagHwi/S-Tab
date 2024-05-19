package com.ssafy.stab.apis.note

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.request.PageId
import com.ssafy.stab.data.note.request.SavingPageData
import com.ssafy.stab.data.note.response.NewPage
import com.ssafy.stab.data.note.response.PageListResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
private val accessToken = PreferencesUtil.getLoginDetails().accessToken
private val authorizationHeader = "Bearer $accessToken"
fun fetchPageList(
    noteId: String,
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
                Log.i("fetchPageList", "$noteId: fetchPageList")
            } else {
                Log.e("fetchPageList", "${response.code()}: $response / $noteId")
            }
        }

        override fun onFailure(call: Call<PageListResponse>, t: Throwable) {
            Log.e("fetchPageList", "err: ${t.localizedMessage}")
        }
    })
}

fun createNewPage(
    beforePageId: String,
    onResponseSuccess: (NewPage) -> Unit
) {
    val call = apiService.createPage(authorizationHeader, PageId(beforePageId))

    call.enqueue(object : Callback<NewPage> {
        override fun onResponse(call: Call<NewPage>, response: Response<NewPage>) {
            if (response.isSuccessful) {
                val responseBody = response.body()!!
                onResponseSuccess(responseBody)
                Log.i("createPage", responseBody.pageId)
            } else {
                Log.e("createPage", "${response.code()}: $response, before: $beforePageId")
            }
        }

        override fun onFailure(call: Call<NewPage>, t: Throwable) {
            Log.e("createPage", "err: ${t.localizedMessage}")
        }
    })
}

fun savePageData(
    pageData: SavingPageData
) {
    val call = apiService.updatePage(authorizationHeader, pageData)

    call.enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                Log.i("savePage", response.body().toString())
            } else {
                Log.e("savePage", "${response.code()}: $response / ${pageData.pageId}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("savePage", "err: ${t.localizedMessage}")
        }
    })
}

fun uploadImageToServer(context: Context, imageUri: Uri, onResponseSuccess: (String) -> Unit) {
    val imgUri = "$imageUri.jpeg"
    val call = apiService.getS3URI(authorizationHeader, imgUri)

    call.enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val url = response.body().toString()
                Log.i("uploadImageToServer", "ok")
                uploadImageToUrl(context, url, imageUri, onResponseSuccess)
            } else {
                Log.e("uploadImageToServer", "Failed to fetch URI: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("uploadImageToServer", "err: ${t.localizedMessage}")
        }
    })
}

fun uploadImageToUrl(context: Context, url: String, imageUri: Uri, onResponseSuccess: (String) -> Unit) {
    val client = OkHttpClient()
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val mediaType = "image/jpeg".toMediaType()

    val requestBody = inputStream?.use { stream ->
        // inputStream에서 바이트 배열을 읽고 RequestBody를 생성
        stream.readBytes().toRequestBody(mediaType)
    }

    if (requestBody != null) {
        val request = Request.Builder()
            .url(url)
            .put(requestBody)  // HTTP PUT 요청을 사용
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("uploadImageToUrl", "Upload failed", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val fullUrl = response.request.url.toString()
                    val imageUrl = fullUrl.split("?").first()
                    Log.d("uploadImageToUrl", "URL: $imageUrl")
                    onResponseSuccess(imageUrl)
                } else {
                    Log.e("uploadImageToUrl", "Upload failed: ${response.message}")
                }
                response.close()  // 응답 리소스 해제
            }
        })
    } else {
        Log.e("UploadError", "Failed to create request body from the input stream.")
    }

}