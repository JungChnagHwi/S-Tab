package com.ssafy.stab.apis.space.share

import com.google.gson.annotations.SerializedName

data class ShareSpaceList(
    @SerializedName("spaceId") val spaceId: String,
    @SerializedName("title") val title: String,
    @SerializedName("public") val public: Boolean,
    @SerializedName("rootFolderId") val rootFolderId: String,
    @SerializedName("users") val users: List<User>,
    @SerializedName("createAt") val createAt: String,
    @SerializedName("updateAt") val updateAt: String
)

data class ShareSpace(
    @SerializedName("spaceId") val spaceId: String,
    @SerializedName("users") val users: List<User>
)

data class User(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImg") val profileImg: String
)

data class CreateShareSpaceRequest(
    @SerializedName("title") val title: String
)

data class ParticipateShareSpaceRequest(
    @SerializedName("spaceId") val spaceId: String
)

data class RenameShareSpaceRequest(
    @SerializedName("spaceId") val spaceId: String,
    @SerializedName("title") val title: String
)

data class MarkdownDataResponse(
    @SerializedName("data") val data: String
)

data class MarkdownDataPatchRequest(
    @SerializedName("spaceId") val spaceId: String,
    @SerializedName("data") val data: String
)