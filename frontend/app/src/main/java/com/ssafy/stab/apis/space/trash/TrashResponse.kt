package com.ssafy.stab.apis.space.trash

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GetTrashListResponse(
    @SerializedName("folders") val folders: List<TrashFolder>,
    @SerializedName("notes") val notes: List<TrashNote>
)

data class TrashFolder(
    @SerializedName("spaceTitle") val spaceTitle: String,
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class TrashNote(
    @SerializedName("spaceTitle") val spaceTitle: String,
    @SerializedName("noteId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class RestoreTrashRequest(
    @SerializedName("id") val id: String
)