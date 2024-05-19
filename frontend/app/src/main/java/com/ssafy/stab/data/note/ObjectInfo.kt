package com.ssafy.stab.data.note

data class Figure(
    val shape: String,
    val color: String,
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float
)

data class TextBox(
    val texts: String,
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float
)

data class ImageInfo(
    val url: String,
    val width: Float = 0f,
    val height: Float = 0f,
    val x: Float,
    val y: Float
)

data class PageImageInfo(
//    var order: Int,
    val userName: String,
    var pageId: String,
    val imageInfo: ImageInfo
)
