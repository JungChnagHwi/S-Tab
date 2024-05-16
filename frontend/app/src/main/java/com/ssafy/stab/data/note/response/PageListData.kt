package com.ssafy.stab.data.note.response

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Figure
import com.ssafy.stab.data.note.Image
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.data.note.TextBox
import java.time.LocalDateTime

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
    val pdfUrl: String?,
    @SerializedName("pdfPage")
    val pdfPage: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    @SerializedName("paths")
    var paths: SnapshotStateList<PathInfo>? = mutableStateListOf(),
    @SerializedName("figures")
    val figures: SnapshotStateList<Figure>? = mutableStateListOf(),
    @SerializedName("textBoxes")
    val textBoxes: SnapshotStateList<TextBox>? = mutableStateListOf(),
    @SerializedName("images")
    val images: SnapshotStateList<Image>? = mutableStateListOf(),
)

data class PageListResponse(
    @SerializedName("title")
    val title: String,
    @SerializedName("data")
    val data: MutableList<PageDetail>
)