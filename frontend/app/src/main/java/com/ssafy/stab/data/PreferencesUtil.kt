package com.ssafy.stab.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PreferencesUtil {
    private lateinit var sharedPreferences: SharedPreferences

    private val _loginDetails = MutableStateFlow<LoginDetails?>(null)
    val loginDetails = _loginDetails.asStateFlow()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppNamePrefs", Context.MODE_PRIVATE)
        loadInitialCallState()

        // 초기화 후 초기 값 설정
        _loginDetails.value = getInitialLoginDetails()

        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "ProfileImg" || key == "UserName") {
                _loginDetails.value = getLoginDetails()
            }
            if (key == "IsInCall" || key == "CallSpaceId") {
                loadInitialCallState()
            }
        }
    }

    fun saveLoginDetails(
        isLoggedIn: Boolean,
        accessToken: String,
        userName: String,
        profileImg: String,
        rootFolderId: String,
        personalSpaceId: String
    ) {
        sharedPreferences.edit {
            putBoolean("IsLoggedIn", isLoggedIn)
            putString("AccessToken", accessToken)
            putString("UserName", userName)
            putString("ProfileImg", profileImg)
            putString("RootFolderId", rootFolderId)
            putString("PersonalSpaceId", personalSpaceId)
            apply()
        }
        _loginDetails.value = getLoginDetails()
    }

    private fun getInitialLoginDetails(): LoginDetails {
        val isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false)
        val accessToken = sharedPreferences.getString("AccessToken", null)
        val userName = sharedPreferences.getString("UserName", null)
        val profileImg = sharedPreferences.getString("ProfileImg", null)
        val rootFolderId = sharedPreferences.getString("RootFolderId", null)
        val personalSpaceId = sharedPreferences.getString("PersonalSpaceId", null)
        return LoginDetails(isLoggedIn, accessToken, userName, profileImg, rootFolderId, personalSpaceId)
    }

    fun getLoginDetails(): LoginDetails {
        val isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false)
        val accessToken = sharedPreferences.getString("AccessToken", null)
        val userName = sharedPreferences.getString("UserName", null)
        val profileImg = sharedPreferences.getString("ProfileImg", null)
        val rootFolderId = sharedPreferences.getString("RootFolderId", null)
        val personalSpaceId = sharedPreferences.getString("PersonalSpaceId", null)
        return LoginDetails(isLoggedIn, accessToken, userName, profileImg, rootFolderId, personalSpaceId)
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
    }

    fun saveShareSpaceState(spaceId: String?) {
        Log.d("PreferencesUtil", "Saving ShareSpaceState: $spaceId")
        sharedPreferences.edit {
            putString("ShareSpace", spaceId)
            apply()
        }
    }

    fun getShareSpaceState(): String? {
        val shareId = sharedPreferences.getString("ShareSpace", null)
        Log.d("PreferencesUtil", "Getting ShareSpaceState: $shareId")
        return shareId
    }

    private val _callState = MutableStateFlow(CallState(false, null))
    val callState = _callState.asStateFlow()
}

data class LoginDetails(
    val isLoggedIn: Boolean,
    val accessToken: String?,
    val userName: String?,
    val profileImg: String?,
    val rootFolderId: String?,
    val personalSpaceId: String?
)

data class NowLocation(
    val nowLocation: String?
)

data class CallState(
    val isInCall: Boolean,
    val callSpaceId: String?,
)
