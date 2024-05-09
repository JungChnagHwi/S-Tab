package com.ssafy.stab.data.note.response

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class Page(
    val pageId: String,
    val direction: Int
)

data class Response(
    val data: MutableList<Page>
)

data class PageDetail(
    @SerializedName("pageId")
    val pageId: String,
    @SerializedName("color")
    val color: BackgroundColor,
    @SerializedName("template")
    val template: TemplateType,
    @SerializedName("direction")
    val direction: Int,
    @SerializedName("isBoolean")
    val isBookmarked: Boolean,
    @SerializedName("pdfUrl")
    val pdfUrl: String,
    @SerializedName("pdfPage")
    val pdfPage: Int,
    @SerializedName("updatedAt")
    val updatedAt: LocalDateTime,
    @SerializedName("paths")
    val paths: MutableList<PathInfo>,
    @SerializedName("figures")
    val figures: MutableList<Any>,
    @SerializedName("textBoxes")
    val textBoxes: MutableList<Any>,
    @SerializedName("images")
    val images: MutableList<Any>,
)

data class PageData(
    @SerializedName("page")
    val page: PageDetail
)

data class PageListResponse(
    @SerializedName("data")
    val data: MutableList<PageData>
)