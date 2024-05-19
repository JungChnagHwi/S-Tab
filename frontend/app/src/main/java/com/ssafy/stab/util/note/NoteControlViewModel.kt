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
import com.ssafy.stab.data.note.Action
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.ImageInfo
import com.ssafy.stab.data.note.PageImageInfo
import com.ssafy.stab.data.note.PageOrderPathInfo
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenSettings
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.SocketPathInfo
import com.ssafy.stab.data.note.UserPagePathInfo
import com.ssafy.stab.screens.note.NoteViewModel
import com.ssafy.stab.util.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NoteControlViewModel(private val noteId: String, private val socketManager: SocketManager) : ViewModel() {
    private lateinit var noteViewModel: NoteViewModel

    private val user = PreferencesUtil.getLoginDetails().userName ?: "unknown"

    private val _undoPathList = mutableStateListOf<PageOrderPathInfo>()
    val pathList: SnapshotStateList<PageOrderPathInfo> = _undoPathList

    private val _newPathList = mutableStateListOf<UserPagePathInfo>()
    val newPathList: SnapshotStateList<UserPagePathInfo> = _newPathList

    private val _redoPathList = mutableStateListOf<PageOrderPathInfo>()

    private val _imageList = mutableStateListOf<PageImageInfo>()
    val imageList: SnapshotStateList<PageImageInfo> = _imageList

    private val _historyTracker = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = _historyTracker.asSharedFlow()

    var scale = mutableFloatStateOf(1f)
        private set

    var offset = mutableStateOf(Offset.Zero)
        private set

    var penType by mutableStateOf(PenType.Pen)
        private set

    private val penSettings = mutableMapOf(
        PenType.Pen to PenSettings("000000", 4f),
        PenType.Highlighter to PenSettings("FFB800", 16f),
        PenType.Eraser to PenSettings("000000", 16f),
    )

    private val _strokeWidth = MutableStateFlow(penSettings[penType]?.strokeWidth ?: 4f)
    val strokeWidth: StateFlow<Float> = _strokeWidth

    private val _color = MutableStateFlow(penSettings[penType]?.color ?: "000000")
    val color: StateFlow<String> = _color

    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable = _undoAvailable.asStateFlow()

    private val _redoAvailable = MutableStateFlow(false)
    val redoAvailable = _redoAvailable.asStateFlow()

    var currentImageUrl by mutableStateOf("")
        private set

    init {
        trackHistory(viewModelScope) { undoCount, redoCount ->
            _undoAvailable.value = undoCount != 0
            _redoAvailable.value = redoCount != 0
        }

        socketManager.setNoteControlViewModel(this)
    }

    fun setNotViewModel(viewModel: NoteViewModel) {
        this.noteViewModel = viewModel
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

    fun setOffset(value: Offset) {
        offset.value = value
    }

    fun changePenType(value: PenType) {
        penType = value
        updatePenSetting()
    }

    fun changeStrokeWidth(value: Float) {
        penSettings[penType]?.strokeWidth = value
        updatePenSetting()
    }

    fun changeColor(value: String) {
        penSettings[penType]?.color = value
        updatePenSetting()
    }

    fun setImageUrl(value: String) {
        currentImageUrl = value
    }

    private fun updatePenSetting() {
        _color.value = penSettings[penType]?.color ?: "000000"
        _strokeWidth.value = penSettings[penType]?.strokeWidth ?: 4f
    }

    fun insertNewPathInfo(currentPageId: String, newCoordinate: Coordinate) {
        val pathInfo = PathInfo(
            penType = penType,
            coordinates = mutableStateListOf(newCoordinate),
            strokeWidth = strokeWidth.value,
            color = color.value
        )

        val userPagePathInfo = UserPagePathInfo(
            user, currentPageId, pathInfo
        )

        _newPathList.add(userPagePathInfo)
        _redoPathList.clear()
    }

    fun updateLatestPath(newCoordinate: Coordinate) {
        if (_newPathList.isNotEmpty()) {
            _newPathList[0].pathInfo.coordinates.add(newCoordinate)
        }
    }

    fun getLastPath(): UserPagePathInfo? {
        return if (_newPathList.isNotEmpty()) {
            _newPathList[0]
        } else null
    }

    fun addNewPath() {
        if (_newPathList.isNotEmpty()) {
            val data = _newPathList[0]
            val order = if (_undoPathList.isNotEmpty()) { _undoPathList.last().order + 1 } else 0
            val pageOrderPathInfo = PageOrderPathInfo(order, data.userName, data.pageId, data.pathInfo)
            if (socketManager.isNoteJoined) {
                socketManager.updateNoteData(noteId, SocketPathInfo(Action.Add, pageOrderPathInfo))
            }
            _undoPathList.add(pageOrderPathInfo)
            _newPathList.clear()
            _historyTracker.tryEmit("insert path")
        }

        noteViewModel.onUserInteraction()
    }

    private fun addOthersPath(othersData: PageOrderPathInfo) {
        if (_undoPathList.isEmpty()) {
            _undoPathList.add(othersData)
        } else {
            val index = _undoPathList.withIndex().filter { it.value.order <= othersData.order }
                .maxByOrNull { it.value.order }?.index ?: -1

            _undoPathList.add(index + 1, othersData)
        }
    }

    fun updatePathsFromSocket(socketPathInfo: SocketPathInfo) {
        val pageOrderPathInfo = socketPathInfo.pageOrderPathInfo

        when (socketPathInfo.type) {
            Action.Add -> {
                addOthersPath(pageOrderPathInfo)
            }
            Action.Undo -> {
                undo(pageOrderPathInfo.userName)
            }
            Action.Create -> TODO()
        }
        noteViewModel.onUserInteraction()
    }

    fun undo(userName: String) {
        val userPathList = _undoPathList.filter { it.userName == userName }
        if (userPathList.isNotEmpty()) {
            val last = userPathList.last()
            val index = _undoPathList.indexOfLast { it.userName == userName }

            // redo 경로 정보 저장
            if (userName == user) {
                if (socketManager.isNoteJoined) {
                    socketManager.updateNoteData(
                        noteId,
                        SocketPathInfo(Action.Undo, last)
                    )
                }
                _redoPathList.add(last)
            }

            // 현재 경로에서 삭제
            _undoPathList.removeAt(index)

            _historyTracker.tryEmit("undo")
        }
        noteViewModel.onUserInteraction()
    }

    fun redo() {
        if (_redoPathList.isNotEmpty()) {
            val last = _redoPathList.last()

            // 경로 복원
            socketManager.updateNoteData(
                noteId, SocketPathInfo(Action.Add, last)
            )
            addOthersPath(last)
            _redoPathList.remove(last)

            _historyTracker.tryEmit("redo")
        }
        noteViewModel.onUserInteraction()
    }

    fun reset() {
        _undoPathList.clear()
        _redoPathList.clear()
        _historyTracker.tryEmit("reset")
    }

    fun getCurrentPathList(currentPageId: String): List<PageOrderPathInfo> {
        return pathList.filter { it.pageId == currentPageId }
    }

    fun trimUndoPathList(): MutableList<PageOrderPathInfo> {
        val autoSaveItems = mutableListOf<PageOrderPathInfo>()
        while (_undoPathList.size > 10) {
            autoSaveItems.add(_undoPathList.removeAt(0))
        }
        return autoSaveItems
    }

    fun addImageToPage(currentPageId: String, x: Float, y: Float) {
        val imageInfo = ImageInfo(currentImageUrl, x = x, y = y)
        val pageImageInfo = PageImageInfo(user, currentPageId, imageInfo)

        _imageList.add(pageImageInfo)
        noteViewModel.onUserInteraction()
    }

}