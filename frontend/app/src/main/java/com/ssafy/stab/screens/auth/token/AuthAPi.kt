package com.ssafy.stab.screens.auth.token

import android.util.Log
import retrofit2.Call
import retrofit2.Response


fun socialLogin(idToken: String) {
    val apiService = RetrofitAuth.getToken().create(ApiService::class.java)
    val idTokenRequest = ApiService.IdTokenRequest(idToken) // idToken을 IdTokenRequest 객체로 변환
    val call = apiService.getTokens(idTokenRequest) // 수정된 호출 방식

    call.enqueue(object : retrofit2.Callback<TokenResponse> {
        override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
            Log.i("APIResponse", "$response")
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()
                Log.i("APIResponse", "Successful response: $authResponse")
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


fun tryLogin(authorization: String) {
    val apiService = RetrofitAuth.getToken().create(ApiService::class.java)
    val call = apiService.getInfoIfUser(authorization)

    call.enqueue(object : retrofit2.Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            when (response.code()) {
                200 -> {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        Log.i("APIResponse", "User info received: $userInfo")
                    } else {
                        Log.e("APIResponse", "Response was successful but no user info found")
                    }
                }
                204 -> {
                    Log.i("APIResponse", "No content: User does not exist or no data available")
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

fun signUp(authorization: String, signupRequest: UserSignupRequest ) {
    val apiService = RetrofitAuth.getToken().create(ApiService::class.java)
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