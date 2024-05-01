package com.ssafy.stab.util.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

class NoteController internal constructor(val trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }) {

    private val undoPathList = mutableStateListOf<PathInfo>()
    internal val pathList: SnapshotStateList<PathInfo> = undoPathList

    var bgColor by mutableStateOf(Color.Black)
        private set

    var strokeWidth by mutableFloatStateOf(10f)
        private set

    var color by mutableStateOf("000000")
        private set

    fun changeBgColor(value: Color) {
        bgColor = value
    }

    fun changeStrokeWidth(value: Float) {
        strokeWidth = value
    }

    fun changeColor(value: String) {
        color = value
    }

    fun insertNewPathInfo(newCoordinate: Coordinate) {
        val pathInfo = PathInfo(
            coordinates = mutableStateListOf(newCoordinate),
            thickness = strokeWidth,
            color = color
        )
        undoPathList.add(pathInfo)
    }

    fun updateLatestPath(newCoordinate: Coordinate) {
        val index = undoPathList.lastIndex
        undoPathList[index].coordinates.add(newCoordinate)
    }

}

@Composable
fun rememberNoteController(): NoteController {
    return remember { NoteController() }
}