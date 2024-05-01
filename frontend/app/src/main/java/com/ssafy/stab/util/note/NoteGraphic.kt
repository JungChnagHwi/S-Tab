package com.ssafy.stab.util.note

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class PathInfo(
    val penType: String = "pen",
    val thickness: Float,
    val color: String,
    var coordinates: SnapshotStateList<Coordinate>,
)

data class Coordinate(
    val x: Float,
    val y: Float
)

fun offsetToCoordinate(offset: Offset): Coordinate {
    return Coordinate(x = offset.x, y = offset.y)
}

fun createPath(coordinates: List<Coordinate>) = Path().apply {
    if (coordinates.size > 1) {
        var beforeCoordinate: Coordinate? = null
        this.moveTo(coordinates[0].x, coordinates[0].y)
        for (i in 1 until coordinates.size) {
            val coordinate: Coordinate = coordinates[i]
            beforeCoordinate?.let {
                val midPoint = calculateMidpoint(it, coordinate)
                if (i == 1) {
                    this.lineTo(midPoint.x, midPoint.y)
                } else {
                    this.quadraticBezierTo(it.x, it.y, midPoint.x, midPoint.y)
                }
            }
            beforeCoordinate = coordinate
        }
        beforeCoordinate?.let { this.lineTo(it.x, it.y) }
    }
}


private fun calculateMidpoint(start: Coordinate, end: Coordinate) =
    Coordinate((start.x + end.x) / 2, (start.y + end.y) / 2)