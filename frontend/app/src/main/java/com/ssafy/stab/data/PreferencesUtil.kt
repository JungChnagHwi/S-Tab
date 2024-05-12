package com.ssafy.stab.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PreferencesUtil {
    private lateinit var sharedPreferences: SharedPreferences
    private val _callState = MutableStateFlow(CallState(false, null))
    val callState = _callState.asStateFlow()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppNamePrefs", Context.MODE_PRIVATE)
        loadInitialCallState()

        // 변화 감지를 위한 리스너 설정
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "IsInCall" || key == "CallSpaceId") {
                loadInitialCallState()
            }
        }
    }

    fun saveLoginDetails(isLoggedIn: Boolean, accessToken: String, userName: String, profileImg: String, rootFolderId: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IsLoggedIn", isLoggedIn)
        editor.putString("AccessToken", accessToken)
        editor.putString("UserName", userName)
        editor.putString("ProfileImg", profileImg)
        editor.putString("RootFolderId", rootFolderId)
        editor.apply()
    }

    fun getLoginDetails(): LoginDetails {
        val isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false)
        val accessToken = sharedPreferences.getString("AccessToken", null)
        val userName = sharedPreferences.getString("UserName", null)
        val profileImg = sharedPreferences.getString("ProfileImg", null)
        val rootFolderId = sharedPreferences.getString("RootFolderId", null)
        return LoginDetails(isLoggedIn, accessToken, userName, profileImg, rootFolderId)
    }

    fun saveLocation(nowLocation: String) {
        val editor = sharedPreferences.edit()
        editor.putString("nowLocation", nowLocation)
        editor.apply()
    }

    fun getNowLocation(): NowLocation {
        val nowLocation = sharedPreferences.getString("nowLocation", "")
        return NowLocation(nowLocation)
    }

    private fun loadInitialCallState() {
        val isInCall = sharedPreferences.getBoolean("IsInCall", false)
        val callSpaceId = sharedPreferences.getString("CallSpaceId", null)
        _callState.value = CallState(isInCall, callSpaceId)
    }

    fun saveCallState(isInCall: Boolean, callSpaceId: String?) {
        sharedPreferences.edit {
            putBoolean("IsInCall", isInCall)
            if (callSpaceId != null) {
                putString("CallSpaceId", callSpaceId)
            } else {
                remove("CallSpaceId")
            }
            apply()
        }
        Log.d("PreferencesUtil", "Call state updated: isInCall = $isInCall, callSpaceId = $callSpaceId")
    }

}

data class LoginDetails(
    val isLoggedIn: Boolean,
    val accessToken: String?,
    val userName: String?,
    val profileImg: String?,
    val rootFolderId: String?
)

data class NowLocation(
    val nowLocation: String?
)

data class CallState(
    val isInCall: Boolean,
    val callSpaceId: String?,
)