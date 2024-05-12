package com.ssafy.stab.data.note.request

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.Figure
import com.ssafy.stab.data.note.Image
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.TextBox

data class PageId(
    @SerializedName("beforePageId")
    val beforePageId: String
)

data class PageInfo(
    @SerializedName("paths")
    val paths: SnapshotStateList<PathInfo> = mutableStateListOf(),
    @SerializedName("figures")
    val figures: SnapshotStateList<Figure> = mutableStateListOf(),
    @SerializedName("textBoxes")
    val textBoxes: SnapshotStateList<TextBox> = mutableStateListOf(),
    @SerializedName("images")
    val images: SnapshotStateList<Image> = mutableStateListOf(),
)

data class PathData(
    @SerializedName("penType")
    val penType: PenType,
    @SerializedName("strokeWidth")
    val strokeWidth: Float,
    @SerializedName("color")
    val color: String,
    @SerializedName("coordinates")
    var coordinates: List<Coordinate>,
)

data class PageData(
    @SerializedName("paths")
    val paths: List<PathData> = listOf(),
    @SerializedName("figures")
    val figures: List<Figure>  = listOf(),
    @SerializedName("textBoxes")
    val textBoxes: List<TextBox> = listOf(),
    @SerializedName("images")
    val images: List<Image> = listOf()
)

data class SavingPageData(
    @SerializedName("pageId")
    val pageId: String,
    @SerializedName("pageData")
    val pageData: String
)

fun convertPathInfoToPathData(pathInfo: PathInfo): PathData {
    return PathData(
        pathInfo.penType,
        pathInfo.strokeWidth,
        pathInfo.color,
        pathInfo.coordinates.toList()
    )
}

fun convertPageInfoToPageData(pageInfo: PageInfo): PageData {
    return PageData(
        pageInfo.paths.map { convertPathInfoToPathData(it) },
        pageInfo.figures.toList(),
        pageInfo.textBoxes.toList(),
        pageInfo.images.toList()
    )
}