package com.ssafy.stab.util.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.UserPagePathInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NoteControlViewModel : ViewModel() {
    private val userName = PreferencesUtil.getLoginDetails().userName ?: "unknown"

    private val _undoPathList = mutableStateListOf<UserPagePathInfo>()
    val pathList: SnapshotStateList<UserPagePathInfo> = _undoPathList

    private val _newPathList = mutableStateListOf<UserPagePathInfo>()
    val newPathList: SnapshotStateList<UserPagePathInfo> = _newPathList

    private val _redoPathList = mutableStateListOf<UserPagePathInfo>()

    private val _historyTracker = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = _historyTracker.asSharedFlow()

    var scale = mutableFloatStateOf(1f)
        private set

    var penType by mutableStateOf(PenType.Pen)
        private set
    var strokeWidth by mutableFloatStateOf(10f)
        private set
    var color by mutableStateOf("000000")
        private set

    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable = _undoAvailable.asStateFlow()

    private val _redoAvailable = MutableStateFlow(false)
    val redoAvailable = _redoAvailable.asStateFlow()

    init {
        trackHistory(viewModelScope) { undoCount, redoCount ->
            _undoAvailable.value = undoCount != 0
            _redoAvailable.value = redoCount != 0
        }
    }

    fun trackHistory(
        scope: CoroutineScope,
        trackHistory: (undoCount: Int, redoCount: Int) -> Unit
    ) {
        historyTracker
            .onEach { trackHistory(_undoPathList.size, _redoPathList.size) }
            .launchIn(scope)
    }

    fun setScale(value: Float) {
        scale.floatValue = value
    }

    fun changePenType(value: PenType) {
        penType = value
    }

    fun changeStrokeWidth(value: Float) {
        strokeWidth = value
    }

    fun changeColor(value: String) {
        color = value
    }

    fun insertNewPathInfo(currentPageId: String, newCoordinate: Coordinate) {
        val pathInfo = PathInfo(
            penType = penType,
            coordinates = mutableStateListOf(newCoordinate),
            strokeWidth = strokeWidth,
            color = color
        )

        val userPagePathInfo = UserPagePathInfo(
            userName, currentPageId, pathInfo
        )

        _newPathList.add(userPagePathInfo)

        _redoPathList.clear()
    }

    fun updateLatestPath(newCoordinate: Coordinate) {
        _newPathList[0].pathInfo.coordinates.add(newCoordinate)
    }

    fun getLastPath(): UserPagePathInfo {
        return _newPathList[0]
    }

    fun addNewPath() {
        _undoPathList.add(_newPathList[0])
        _newPathList.clear()
        _historyTracker.tryEmit("insert path")
    }

    fun addOthersPath(userPagePathInfo: UserPagePathInfo) {
        _undoPathList.add(userPagePathInfo)
    }

    fun undo() {
        val userPathList = _undoPathList.filter { it.userName == userName }
        if (userPathList.isNotEmpty()) {
            val last = userPathList.last()
            val index = _undoPathList.indexOfLast { it.userName == userName }

            // redo 경로 정보 저장
            _redoPathList.add(last)

            // 현재 경로에서 삭제
            _undoPathList.removeAt(index)

            _historyTracker.tryEmit("undo")
        }
    }

    fun redo() {
        if (_redoPathList.isNotEmpty()) {
            val last = _redoPathList.last()

            // 경로 복원
            _undoPathList.add(last)
            _redoPathList.remove(last)

            _historyTracker.tryEmit("redo")
        }
    }

    fun reset() {
        _undoPathList.clear()
        _redoPathList.clear()
        _historyTracker.tryEmit("reset")
    }

    fun getCurrentPathList(currentPageId: String): List<UserPagePathInfo> {
        return pathList.filter { it.pageId == currentPageId }
    }

}