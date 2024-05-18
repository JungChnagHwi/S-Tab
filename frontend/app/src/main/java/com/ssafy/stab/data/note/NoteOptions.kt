package com.ssafy.stab.data.note

enum class PenType {
    Pen,
    Highlighter,
    Eraser,
    Lasso,
    Image
}

data class PenSettings(
    var color: String,
    var strokeWidth: Float
)

enum class TemplateType {
    Plain,
    Lined,
    Grid
}

enum class BackgroundColor {
    White,
    Yellow
}

enum class Direction {
    Portrait,
    Landscape
}

enum class Shape {
    Circle,
    Square,
    Triangle
}