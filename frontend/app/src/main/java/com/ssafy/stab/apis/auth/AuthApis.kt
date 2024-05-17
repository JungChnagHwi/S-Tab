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
                    if (userInfo != null) {
                        Log.i("APIResponse", "User info received: $userInfo")
                        PreferencesUtil.saveLoginDetails(
                            isLoggedIn = true,
                            accessToken = accessToken,
                            userName = userInfo.nickname,
                            profileImg = userInfo.profileImg,
                            rootFolderId = userInfo.rootFolderId
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
                        rootFolderId = ""
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
                    rootFolderId = authResponse.rootFolderId
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