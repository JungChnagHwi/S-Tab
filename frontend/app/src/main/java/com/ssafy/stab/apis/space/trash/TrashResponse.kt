package com.ssafy.stab.apis.space.trash

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class GetTrashListResponse(
    @SerializedName("folders") val folders: List<TrashFolder>,
    @SerializedName("notes") val notes: List<TrashNote>,
    @SerializedName("pages") val pages: List<TrashPage>
)

data class TrashFolder(
    @SerializedName("spaceTitle") val spaceTitle: String,
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class TrashNote(
    @SerializedName("spaceTitle") val spaceTitle: String,
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class TrashPage(
    @SerializedName("pageId") val pageId: String,
    @SerializedName("color") val color: BackgroundColor,
    @SerializedName("template") val template: TemplateType,
    @SerializedName("direction") val direction: Int,
    @SerializedName("isBookmarked") val isBookmarked: Boolean,
    @SerializedName("pdfUrl") val pdfUrl: String,
    @SerializedName("pdfPage") val pdfPage: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class RestoreTrashRequest(
    @SerializedName("id") val id: String
)