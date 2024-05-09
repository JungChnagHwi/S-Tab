package com.ssafy.stab.apis.space.folder

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class FileListResponse(
    @SerializedName("folders") val folders: List<Folder>,
    @SerializedName("notes") val notes: List<Note>
)

data class Folder(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean
)

data class Note(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean
)

data class CreateFolderRequest(
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("title") val title: String
)

data class RenameFolderRequest(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String
)

data class RelocateFolderRequest(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("parentFolderId") val parentFolderId: String
)