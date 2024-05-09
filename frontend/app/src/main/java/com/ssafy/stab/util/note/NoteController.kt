package com.ssafy.stab.util.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.data.note.response.PageData
import com.ssafy.stab.data.note.response.PageDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteController internal constructor(val trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }) {

    private val undoPageList = mutableStateListOf<Int>()
    private val redoPageList = mutableStateListOf<Int>()
    private val redoPathList = mutableStateListOf<PathInfo>()

    private val historyTracking = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = historyTracking.asSharedFlow()

    fun trackHistory(
        scope: CoroutineScope,
        trackHistory: (undoCount: Int, redoCount: Int) -> Unit
    ) {
        historyTracker
            .onEach { trackHistory(undoPageList.size, redoPageList.size) }
            .launchIn(scope)
    }

    var penType by mutableStateOf(PenType.Pen)
        private set

    fun changePenType(value: PenType) {
        penType = value
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

    fun insertNewPathInfo(currentPage: Int, newCoordinate: Coordinate, paths: MutableList<PathInfo>) {
        val pathInfo = PathInfo(
            penType = penType,
            coordinates = mutableStateListOf(newCoordinate),
            strokeWidth = strokeWidth,
            color = color
        )

        paths.add(pathInfo)
        undoPageList.add(currentPage)

        redoPageList.clear()
        redoPathList.clear()

        historyTracking.tryEmit("insert path")
    }

    fun updateLatestPath(newCoordinate: Coordinate, paths: MutableList<PathInfo>) {
        val index = paths.lastIndex
        paths[index].coordinates.add(newCoordinate)
    }

    fun getLastPath(paths: MutableList<PathInfo>): PathInfo {
        return paths.last()
    }

    fun undo(pageList: MutableList<PageData>) {
        if (undoPageList.isNotEmpty() && pageList.isNotEmpty()) {
            val page = undoPageList.last()
            val paths = pageList[page].page.paths
            val last = paths?.last()

            // redo 경로 정보 저장
            redoPageList.add(page)
            if (last != null) {
                redoPathList.add(last)
            }

            // 현재 경로에서 삭제
            paths?.remove(last)
            undoPageList.remove(page)

            trackHistory(undoPageList.size, redoPageList.size)
            historyTracking.tryEmit("undo")
        }
    }

    fun redo(pageList: MutableList<PageData>) {
        if (redoPageList.isNotEmpty() && redoPathList.isNotEmpty()) {
            val page = redoPageList.last()
            val last = redoPathList.last()
            val paths = pageList[page].page.paths

            // 경로 복원
            paths?.add(last)

            // undo 경로 정보 저장
            undoPageList.add(page)

            redoPathList.remove(last)
            redoPageList.remove(page)

            trackHistory(undoPageList.size, redoPageList.size)
            historyTracking.tryEmit("redo")
        }
    }

    fun reset() {
        undoPageList.clear()
        redoPageList.clear()
        redoPathList.clear()
        historyTracking.tryEmit("reset")
    }

    fun createPage(currentPage: Int, pageList: MutableList<PageData>) {
        val pageDetail = PageDetail(
            pageId = "p-어쩌고",
            color = BackgroundColor.White,
            template = TemplateType.Grid,
            direction = 1,
            isBookmarked = false,
            pdfUrl = null,
            pdfPage = null,
            updatedAt = LocalDateTime.parse("2024-05-08 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            paths = null,
            figures = null,
            textBoxes = null,
            images = null
        )

        val pageData = PageData(page = pageDetail)

        if (currentPage + 1 >= pageList.size) {
            pageList.add(pageData)
        } else {
            pageList.add(currentPage + 1, pageData)
        }

        // 생성된 페이지 이후 undo 페이지 번호 하나씩 뒤로 밀기
        undoPageList.forEachIndexed { i, value ->
            if (value > currentPage) {
                undoPageList[i] = value + 1
            }
        }
    }

}

@Composable
fun rememberNoteController(): NoteController {
    return remember { NoteController() }
}