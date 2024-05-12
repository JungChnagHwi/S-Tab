package com.ssafy.stab.screens.auth

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.stab.apis.auth.socialLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StaticFieldLeak")
class KakaoAuthViewModel(application: Application): AndroidViewModel(application) {
           companion object {
        const val TAG = "KakaoAuthViewModel"
    }

    private val context = application.applicationContext

    val isLoggedIn = MutableStateFlow(false)

    fun kakaoLogin(navController: NavController){
        viewModelScope.launch {
            isLoggedIn.emit(handleKakaoLogin(navController = navController))
        }
    }

    fun kakaoLogout(){
        viewModelScope.launch {
            if (handleKakaoLogout()) {
                isLoggedIn.emit(false)
            }
        }
    }

    private suspend fun handleKakaoLogout(): Boolean =
        suspendCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    continuation.resume(false)
                }
                else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    continuation.resume(true)
                }
            }
        }

    private suspend fun handleKakaoLogin(navController: NavController) : Boolean =
        suspendCoroutine { continuation ->
        // 로그인 조합 예제

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
                continuation.resume(false)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.idToken}")
                continuation.resume(true)
                socialLogin(token.idToken.toString(), navController = navController)
            }
        }
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}