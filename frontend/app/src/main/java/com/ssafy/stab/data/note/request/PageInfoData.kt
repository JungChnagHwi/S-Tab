package com.ssafy.stab.data.note.request

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.Figure
import com.ssafy.stab.data.note.ImageInfo
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.TextBox

data class PageId(
    @SerializedName("beforePageId")
    val beforePageId: String
)

data class PageData(
    @SerializedName("paths")
    val paths: SnapshotStateList<PathInfo> = mutableStateListOf(),
    @SerializedName("figures")
    val figures: SnapshotStateList<Figure> = mutableStateListOf(),
    @SerializedName("textBoxes")
    val textBoxes: SnapshotStateList<TextBox> = mutableStateListOf(),
    @SerializedName("images")
    val images: SnapshotStateList<ImageInfo> = mutableStateListOf(),
)

data class SavingPageData(
    @SerializedName("pageId")
    val pageId: String,
    @SerializedName("pageData")
    val pageData: PageData
)