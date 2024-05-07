package com.ssafy.stab.apis.auth

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

data class TokenResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
)