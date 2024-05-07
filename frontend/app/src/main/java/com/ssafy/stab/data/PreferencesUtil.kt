package com.ssafy.stab.data

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtil {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppNamePrefs", Context.MODE_PRIVATE)
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
}

data class LoginDetails(
    val isLoggedIn: Boolean,
    val accessToken: String?,
    val userName: String?,
    val profileImg: String?,
    val rootFolderId: String?
)
