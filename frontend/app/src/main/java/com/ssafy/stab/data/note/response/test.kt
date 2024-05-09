package com.ssafy.stab.data.note.response

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class NotePageData(
    @SerializedName("pageId")
    val pageId: String,
    @SerializedName("color")
    val color: BackgroundColor,
    @SerializedName("template")
    val template: TemplateType,
    @SerializedName("direction")
    val direction: Int,
    @SerializedName("isBookmarked")
    val isBookmarked: Boolean,
    @SerializedName("updatedAt")
    val updatedAt: LocalDateTime
)

data class NoteResponse(
    @SerializedName("noteId")
    val noteId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("totalPageCnt")
    val totalPageCnt: Int,
    @SerializedName("updatedAt")
    val updatedAt: LocalDateTime,
    @SerializedName("isLiked")
    val isLiked: Boolean,
    @SerializedName("page")
    val page: NotePageData
)