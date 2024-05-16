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

@Composable
fun NoteArea(
    currentPageId: String,
    paths: SnapshotStateList<PathInfo>?,
    modifier: Modifier,
    viewModel: NoteControlViewModel
) {
    Box(
        modifier = modifier
    ) {
        var isStylus by remember { mutableStateOf(false) }

        val touchAwareModifier = Modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach {change ->
                            isStylus = change.type != PointerType.Touch
                        }
                    }
                }
            }

        val canvasModifier = Modifier
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = { offset ->
//                        val coordinate = offsetToCoordinate(offset)
//                        viewModel.insertNewPathInfo(currentPageId, coordinate)
//                        viewModel.updateLatestPath(coordinate)
//                        viewModel.addNewPath()
//                    }
//                )
//            }
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
            modifier = Modifier
                .fillMaxSize()
                .then(touchAwareModifier)
                .then(if (isStylus) canvasModifier else Modifier)
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                paths?.forEach { pathInfo ->
                    when (pathInfo.penType) {
                        PenType.Pen -> {
                            drawPath(
                                path = createPath(pathInfo.coordinates),
                                color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                                style = Stroke(
                                    width = pathInfo.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        PenType.Highlighter -> {
                            drawPath(
                                path = createPath(pathInfo.coordinates),
                                color = Color(color = ("40" + pathInfo.color).toLong(16)),
                                style = Stroke(
                                    width = pathInfo.strokeWidth,
                                    cap = StrokeCap.Square,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        else -> {
                            drawPath(
                                path = createPath(pathInfo.coordinates),
                                color = Color(color = ("00" + pathInfo.color).toLong(16)),
                                style = Stroke(
                                    width = pathInfo.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }
                }
                if (viewModel.getCurrentPathList(currentPageId).isNotEmpty()) {
                    viewModel.getCurrentPathList(currentPageId)
                        .forEach { userPagePathInfo ->
                            val pathInfo = userPagePathInfo.pathInfo
                            when (pathInfo.penType) {
                                PenType.Pen -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("FF" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                PenType.Highlighter -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("40" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Square,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                else -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("00" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        ),
                                        blendMode = BlendMode.Clear
                                    )
                                }
                            }
                        }
                }
                if (viewModel.newPathList.isNotEmpty()) {
                    viewModel.newPathList.forEach { userPagePathInfo ->
                        if (userPagePathInfo.pageId == currentPageId) {
                            val pathInfo = userPagePathInfo.pathInfo
                            when (pathInfo.penType) {
                                PenType.Pen -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("FF" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                PenType.Highlighter -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("40" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Square,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }

                                else -> {
                                    drawPath(
                                        path = createPath(pathInfo.coordinates),
                                        color = Color(
                                            color = ("00" + pathInfo.color).toLong(
                                                16
                                            )
                                        ),
                                        style = Stroke(
                                            width = pathInfo.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        ),
                                        blendMode = BlendMode.Clear
                                    )
                                }
                            }
                        }
                    }
                }
                restoreToCount(checkPoint)
            }
        }
    }
}