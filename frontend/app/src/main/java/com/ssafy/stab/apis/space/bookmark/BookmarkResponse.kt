package com.ssafy.stab.apis.space.bookmark

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class BookmarkListResponse(
    @SerializedName("folders") val folders: List<BookmardFolder>,
    @SerializedName("notes") val notes: List<BookmardNote>,
    @SerializedName("pages") val pages: List<BookmardPage>
)

data class BookmardFolder(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("rootFolderId") val rootFolderId: String
)

data class BookmardNote(
    @SerializedName("noteId") val noteId: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPageCnt") val totalPageCnt: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("spaceId") val spaceId: String
)

data class BookmardPage(
    @SerializedName("pageId") val pageId: String,
    @SerializedName("color") val color: BackgroundColor,
    @SerializedName("template") val template: TemplateType,
    @SerializedName("direction") val direction: Int,
    @SerializedName("isBookmarked") val isBookmarked: Boolean,
    @SerializedName("pdfUrl") val pdfUrl: String,
    @SerializedName("pdfPage") val pdfPage: Int,
    @SerializedName("updatedAt") val updatedAt: LocalDateTime,
    @SerializedName("spaceId") val spaceId: String,
    @SerializedName("noteTitle") val noteTitle: String
)

data class AddBookmarkRequest(
    @SerializedName("id") val id: String
)

data class GetPathRequest(
    @SerializedName("parentFolderId") val parentFolderId: String,
    @SerializedName("folderId") val folderId: String
)

data class GetPathResponse(
    @SerializedName("folders") val folders: List<PathResponse>
)

data class PathResponse(
    @SerializedName("folderId") val folderId: String,
    @SerializedName("title") val title: String
)