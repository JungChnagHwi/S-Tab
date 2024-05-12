package com.ssafy.stab.data.note

import androidx.compose.runtime.snapshots.SnapshotStateList

data class PathInfo(
    val penType: PenType,
    val strokeWidth: Float,
    val color: String,
    var coordinates: SnapshotStateList<Coordinate>,
)

data class Coordinate(
    val x: Float,
    val y: Float
)

data class UserPagePathInfo(
    val userName: String,
    var pageId: String,
    val pathInfo: PathInfo
)
