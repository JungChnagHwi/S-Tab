package com.ssafy.stab.apis.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.ssafy.stab.apis.RetrofitClient
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.auth.uploadFile
import retrofit2.Call
import retrofit2.Response


fun socialLogin(idToken: String, navController: NavController) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val idTokenRequest = ApiService.IdTokenRequest(idToken) // idToken을 IdTokenRequest 객체로 변환
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
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = authorization
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getInfoIfUser(authorizationHeader)

    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            when (response.code()) {
                200 -> {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        Log.i("APIResponse", "User info received: $userInfo")
                        PreferencesUtil.saveLoginDetails(
                            isLoggedIn = true,
                            accessToken = accessToken,
                            userName = userInfo.nickname,
                            profileImg = userInfo.profileImg,
                            rootFolderId = userInfo.rootFolderId
                        )
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
                        rootFolderId = ""
                    )
                    navController.navigate("sign-up")
                }
                else -> {
                    // Handle other unexpected status codes
                    Log.e("APIResponse", "Unexpected response code: ${response.code()}")
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

fun signUp(authorization: String, signupRequest: UserSignupRequest) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val call = apiService.getInfoNewUser(authorization, signupRequest)

    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                Log.i("APIResponse", "Successful response: $authResponse")
            } else {
                Log.e("APIResponse", "API Call failed!")
            }
        }
        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun s3uri(context: Context, imageUri: Uri) {
    val apiService = RetrofitClient.instance.create(ApiService::class.java)
    val accessToken = PreferencesUtil.getLoginDetails().accessToken
    val authorizationHeader = "Bearer $accessToken"
    val call = apiService.getS3URI(authorizationHeader, imageUri.toString())  // 예제에서는 imageUri를 직접 사용하고 있으나, 실제 파일 이름이 필요합니다.

    call.enqueue(object : retrofit2.Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val presignedUrl = response.body().toString()
                Log.i("APIResponse", "Presigned URL: $presignedUrl")
                // Presigned URL을 받았으니, 이제 이미지를 업로드합니다.
                uploadFile(context, presignedUrl, imageUri)
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