package com.ssafy.stab.apis.space.note

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class CreateNoteRequest(
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("color") val color: BackgroundColor,
    @SerializedName("template") val template: TemplateType,
    @SerializedName("direction") val direction: Int
)

data class CreateNoteResponse(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean,
    @SerializedName("page") val page: Page
)

data class Page(
    @SerializedName("pageId") val pageId: String,
    @SerializedName("color") val color: BackgroundColor,
    @SerializedName("template") val template: TemplateType,
    @SerializedName("direction") val direction: Int,
    @SerializedName("isBookmarked") val isBookmarked: Boolean,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime
)

data class CreatePdfNoteRequest(
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("pdfUrl") val pdfUrl: String,
    @SerializedName("pdfPageCount") val pdfPageCount: Int,
    @SerializedName("title") val title: String
)

data class CopyNoteRequest(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("title") val title: String
)

data class CopyNoteResponse(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("isLiked") val isLiked: Boolean,
)

data class RenameNoteRequest(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String
)

data class RelocateRequest(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("parentFolderId") val parentFolderId: String
)