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

data class Image(
    val url: String,
    val width: Float,
    val height: Float,
    val x: Float,
    val y: Float
)