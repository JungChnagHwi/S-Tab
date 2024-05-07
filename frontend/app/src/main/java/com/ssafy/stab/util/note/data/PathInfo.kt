package com.ssafy.stab.util.note.data

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