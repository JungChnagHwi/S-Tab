package com.ssafy.stab.util.note

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.util.SocketManager

@Composable
fun NoteArea(
    currentPageId: String,
    paths: SnapshotStateList<PathInfo>?,
    modifier: Modifier,
    viewModel: NoteControlViewModel,
) {
    Box(
        modifier = modifier
    ) {
        val canvasModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        if (down.type == PointerType.Stylus) {
                            val initialOffset = down.position
                            if (viewModel.penType != PenType.Lasso) {
                                val coordinate = offsetToCoordinate(initialOffset)
                                viewModel.insertNewPathInfo(currentPageId, coordinate)
                            } else {
                                // 올가미
                            }
                            do {
                                val event = awaitPointerEvent(PointerEventPass.Main)
                                event.changes.forEach { change ->
                                    val coordinate = offsetToCoordinate(change.position)
                                    if (viewModel.penType != PenType.Lasso) {
                                        if (change.pressed) {
                                            viewModel.updateLatestPath(coordinate)
                                            change.consume()
                                        }
                                    } else {
                                        // 올가미
                                    }
                                }
                            } while (event.changes.any { it.pressed })

                            viewModel.addNewPath()
                        }
                    }
                }
            }

        Canvas(
            modifier = canvasModifier
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                val drawPathInfo: (PathInfo) -> Unit = { pathInfo ->
                    val colorPrefix = when (pathInfo.penType) {
                        PenType.Pen -> "FF"
                        PenType.Highlighter -> "40"
                        else -> "00"
                    }

                    val strokeStyle = Stroke(
                        width = pathInfo.strokeWidth,
                        cap = if (pathInfo.penType == PenType.Highlighter) StrokeCap.Square else StrokeCap.Round,
                        join = StrokeJoin.Round
                    )

                    val blendMode = if (pathInfo.penType == PenType.Eraser) BlendMode.Clear else BlendMode.SrcOver

                    drawPath(
                        path = createPath(pathInfo.coordinates),
                        color = Color(color = (colorPrefix + pathInfo.color).toLong(16)),
                        style = strokeStyle,
                        blendMode = blendMode
                    )
                }

                paths?.forEach { pathInfo ->
                    drawPathInfo(pathInfo)
                }

                if (viewModel.getCurrentPathList(currentPageId).isNotEmpty()) {
                    viewModel.getCurrentPathList(currentPageId).forEach { userPagePathInfo ->
                        drawPathInfo(userPagePathInfo.pathInfo)
                    }
                }

                if (viewModel.newPathList.isNotEmpty()) {
                    viewModel.newPathList.forEach { userPagePathInfo ->
                        if (userPagePathInfo.pageId == currentPageId) {
                            drawPathInfo(userPagePathInfo.pathInfo)
                        }
                    }
                }
                restoreToCount(checkPoint)
            }
        }
    }
}