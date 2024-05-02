package com.ssafy.stab.util.note

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.ssafy.stab.util.note.data.Coordinate
import com.ssafy.stab.util.note.data.PathInfo
import com.ssafy.stab.util.note.data.PenType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NoteController internal constructor(val trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }) {

    private val undoPathList = mutableStateListOf<PathInfo>()
    private val redoPathList = mutableStateListOf<PathInfo>()
    internal val pathList: SnapshotStateList<PathInfo> = undoPathList

    private val historyTracking = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = historyTracking.asSharedFlow()

    fun trackHistory(
        scope: CoroutineScope,
        trackHistory: (undoCount: Int, redoCount: Int) -> Unit
    ) {
        historyTracker
            .onEach { trackHistory(undoPathList.size, redoPathList.size) }
            .launchIn(scope)
    }

    var bgColor by mutableStateOf(Color.White)
        private set

    fun changeBgColor(value: Color) {
        bgColor = value
    }

    var penType by mutableStateOf(PenType.Pen)
        private set

    fun changePenType(value: PenType) {
        penType = value
        Log.d("type", "$penType")
    }

    var strokeWidth by mutableFloatStateOf(10f)
        private set

    fun changeStrokeWidth(value: Float) {
        strokeWidth = value
    }

    var color by mutableStateOf("000000")
        private set

    fun changeColor(value: String) {
        color = value
    }

    fun insertNewPathInfo(newCoordinate: Coordinate) {
        val pathInfo = PathInfo(
            penType = penType,
            coordinates = mutableStateListOf(newCoordinate),
            strokeWidth = strokeWidth,
            color = color
        )
        undoPathList.add(pathInfo)
        redoPathList.clear()
        historyTracking.tryEmit("insert path")
    }

    fun updateLatestPath(newCoordinate: Coordinate) {
        val index = undoPathList.lastIndex
        undoPathList[index].coordinates.add(newCoordinate)
    }

    fun undo() {
        if (undoPathList.isNotEmpty()) {
            val last = undoPathList.last()
            redoPathList.add(last)
            undoPathList.remove(last)
            trackHistory(undoPathList.size, redoPathList.size)
            historyTracking.tryEmit("undo")
        }
    }

    fun redo() {
        if (redoPathList.isNotEmpty()) {
            val last = redoPathList.last()
            undoPathList.add(last)
            redoPathList.remove(last)
            trackHistory(undoPathList.size, redoPathList.size)
            historyTracking.tryEmit("redo")
        }
    }

}

@Composable
fun rememberNoteController(): NoteController {
    return remember { NoteController() }
}