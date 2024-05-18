package com.ssafy.stab.apis.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.ssafy.stab.BuildConfig
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.auth.uploadFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)


fun socialLogin(idToken: String, navController: NavController) {
    val idTokenRequest = IdTokenRequest(idToken) // idToken을 IdTokenRequest 객체로 변환
    val call = apiService.getTokens(idTokenRequest) // 수정된 호출 방식

    call.enqueue(object : retrofit2.Callback<TokenResponse> {
        override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
            Log.i("APIResponse", "$response")
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                Log.i("APIResponse", "Successful response: ${authResponse?.accessToken}")
                val accessToken = authResponse?.accessToken
                tryLogin(accessToken.toString(), navController = navController)
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }
        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
            Log.e("APIResponse", "Error on API call", t)
            t.printStackTrace()
        }
    })
}


fun tryLogin(authorization: String, navController: NavController) {
    val accessToken = authorization
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getInfoIfUser(authorizationHeader)
    Log.d("a", authorizationHeader)
    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            Log.d("a", response.toString())
            when (response.code()) {
                200 -> {
                    val userInfo = response.body()
                    Log.d("login", "${response.body()}")
                    if (userInfo != null) {
                        Log.i("APIResponse", "User info received: $userInfo")
                        PreferencesUtil.saveLoginDetails(
                            isLoggedIn = true,
                            accessToken = accessToken,
                            userName = userInfo.nickname,
                            profileImg = userInfo.profileImg,
                            rootFolderId = userInfo.rootFolderId,
                            personalSpaceId = userInfo.privateSpaceId
                        )
                        PreferencesUtil.saveLocation(nowLocation = userInfo.rootFolderId)
                        navController.navigate("space")
                    } else {
                        Log.e("APIResponse", "Response was successful but no user info found")
                    }
                }
                204 -> {
                    Log.i("APIResponse", "No content: User does not exist or no data available")
                    PreferencesUtil.saveLoginDetails(
                        isLoggedIn = false,
                        accessToken = accessToken,
                        userName = "",
                        profileImg = "",
                        rootFolderId = "",
                        personalSpaceId = ""
                    )
                    navController.navigate("sign-up")
                }
                else -> {
                    // Handle other unexpected status codes
                    Log.e("APIResponse", "Unexpected response code: ${response}")
                }
            }
        }

        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            // Handle failure such as network error
            Log.e("APIError", "Failed to connect to the server: ${t.localizedMessage}")
            t.printStackTrace()
        }
    })
}

fun signUp(nickname: String, profileImg: String) {
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val userSignupRequest = UserSignupRequest(nickname, profileImg)
    val call = apiService.getInfoNewUser(authorizationHeader, userSignupRequest)

    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                Log.i("APIResponse", "Successful response: $authResponse")
                if (authResponse != null) {
                    Log.i("rootFolderId", authResponse.rootFolderId)
                }
                PreferencesUtil.saveLoginDetails(
                    isLoggedIn = true,
                    accessToken = accessToken!!,
                    userName = authResponse!!.nickname,
                    profileImg = authResponse.profileImg,
                    rootFolderId = authResponse.rootFolderId,
                    personalSpaceId = authResponse.privateSpaceId
                )
                PreferencesUtil.saveLocation(nowLocation = authResponse.rootFolderId)
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }
        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun s3uri(context: Context, imageUri: Uri, nickname: String) {
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val imgUri = "$imageUri.jpeg"
    val call = apiService.getS3URI(authorizationHeader, imgUri)

    call.enqueue(object : retrofit2.Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val presignedUrl = response.body().toString()
                Log.i("APIResponse", "Presigned URL: $presignedUrl")
                // Presigned URL을 받았으니, 이제 이미지를 업로드합니다.
                uploadFile(context, presignedUrl, imageUri, nickname)
            } else {
                Log.e("APIResponse", "Failed to fetch URI: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("APIError", "Failed to connect to the server: ${t.localizedMessage}")
            t.printStackTrace()
        }
    })
}

fun checkNickName(nickname: String, onResult: (Boolean) -> Unit) {
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.checkNickname(authorizationHeader, nickname)

    call.enqueue(object : retrofit2.Callback<NickNameResponse> {
        override fun onResponse(call: Call<NickNameResponse>, response: Response<NickNameResponse>) {
            if (response.isSuccessful && response.body() != null) {
                // 결과가 0이면 닉네임 사용 가능, 1이면 사용 불가능
                onResult(response.body()!!.result == 0)
            } else {
                Log.e("APIResponse", "API Call failed!")
                onResult(false)
            }
        }

        override fun onFailure(call: Call<NickNameResponse>, t: Throwable) {
            Log.e("APIError", "Failed to connect to the server")
            onResult(false)
        }
    })
}

fun patchInfo(nickname: String, profileImg: String, onResult: (AuthResponse) -> Unit) {
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val patchInfoRequest = PatchInfoRequest(nickname, profileImg)
    val call = apiService.patchInfo(authorizationHeader, patchInfoRequest)

    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            if (response.isSuccessful) {
                response.body()?.let { onResult(it) }
            } else {
                Log.d("회원정보수정", response.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            Log.d("회원정보수정", "요청 실패")
        }
    })
}

fun s3uriForPatch(context: Context, imageUri: Uri, nickname: String, onResult: (String) -> Unit) {
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val imgUri = "$imageUri.jpeg"
    val call = apiService.getS3URI(authorizationHeader, imgUri)

    call.enqueue(object : retrofit2.Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val presignedUrl = response.body().toString()
                Log.i("s3", "Presigned URL: $presignedUrl")
                // Presigned URL을 받았으니, 이제 이미지를 업로드합니다.
                uploadFileForPatch(context, presignedUrl, imageUri, nickname) {res ->
                    Log.d("잘 왔나?", res)
                    onResult(res)
                }
            } else {
                Log.e("s3", "Failed to fetch URI: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("s3", "Failed to connect to the server: ${t.localizedMessage}")
            t.printStackTrace()
        }
    })
}

fun uploadFileForPatch(context: Context, url: String, imageUri: Uri, nickname: String, onResult: (String) -> Unit) {
    val client = OkHttpClient()
    Log.d("a", imageUri.toString())
    if (imageUri.toString() != "null") {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val mediaType = "image/jpeg".toMediaType()

        val body = inputStream?.use { stream ->
            // inputStream에서 바이트 배열을 읽고 RequestBody를 생성
            RequestBody.create(mediaType, stream.readBytes())
        }

        if (body != null) {  // Body가 null이 아니면 요청 실행
            val request = Request.Builder()
                .url(url)
                .put(body)  // HTTP PUT 요청을 사용
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Log.e("Upload", "Upload failed", e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val fullUrl = response.request.url.toString()
                        val baseUrl = fullUrl.split("?").first()
                        Log.d("Upload", "Base URL: $baseUrl")
                        Log.d("Upload", "Upload was successful")
                        onResult(baseUrl)
                    } else {
                        Log.e("Upload", "Upload failed: ${response.message}")
                    }
                    response.close()  // 응답 리소스 해제
                }
            })
        } else {
            Log.e("UploadError", "Failed to create request body from the input stream.")
        }
    } else {
        val baseImage = BuildConfig.BASE_S3 + "/image/2024/05/08/3454673260/profileImage.png"
        signUp(nickname, baseImage)
    }
}