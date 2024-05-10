package com.ssafy.stab.data.note.response

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.TemplateType
import java.time.LocalDateTime

data class PageData(
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
    val pdfUrl: String?,
    @SerializedName("pdfPage")
    val pdfPage: Int?,
    @SerializedName("updatedAt")
    val updatedAt: LocalDateTime,
    @SerializedName("paths")
    val paths: SnapshotStateList<PathInfo>?,
    @SerializedName("figures")
    val figures: MutableList<Any>?,
    @SerializedName("textBoxes")
    val textBoxes: MutableList<Any>?,
    @SerializedName("images")
    val images: MutableList<Any>?,
)

data class PageListResponse(
    @SerializedName("data")
    val data: MutableList<PageData>
)