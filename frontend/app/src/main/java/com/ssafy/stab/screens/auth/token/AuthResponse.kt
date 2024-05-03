package com.ssafy.stab.screens.auth.token

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg")  val profileImg: String,
    @SerializedName("rootFolderId") val rootFolderId: String
)


data class UserSignupRequest(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg") val profileImg: String
)