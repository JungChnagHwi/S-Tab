package com.ssafy.stab.apis.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg")  val profileImg: String,
    @SerializedName("rootFolderId") val rootFolderId: String,
    @SerializedName("privateSpaceId") val privateSpaceId: String
)

data class TokenResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
)

data class NickNameResponse(
    @SerializedName("result") val result: Int
)

data class IdTokenRequest(
    @SerializedName("idToken")
    val idToken: String
)

data class UserSignupRequest(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg") val profileImg: String
)