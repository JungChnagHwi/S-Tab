package com.ssafy.stab.apis.space.folder

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

interface FileEntity {
    val updatedAt: LocalDateTime
}
data class FileListResponse(
    @SerializedName("folders") val folders: List<Folder>,
    @SerializedName("notes") val notes: List<Note>
)

data class Folder(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") override val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean
) : FileEntity

data class Note(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") override val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean
) : FileEntity


data class CreateFolderRequest(
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("title") val title: String
)

data class RenameFolderRequest(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("newTitle") val newTitle: String
)

data class RelocateFolderRequest(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("parentFolderId") val parentFolderId: String
)