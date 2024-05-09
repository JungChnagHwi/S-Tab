package com.ssafy.stab.util.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType

@Composable
fun NoteArea(
    index: Int,
    paths: SnapshotStateList<PathInfo>,
    noteController: NoteController,
) = AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = {
        ComposeView(it).apply {
            setContent {
                val canvasModifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val coordinate = offsetToCoordinate(offset)
                                noteController.insertNewPathInfo(index, coordinate, paths)
                                noteController.updateLatestPath(coordinate, paths)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val coordinate = offsetToCoordinate(offset)
                                if (noteController.penType != PenType.Lasso) {
                                    noteController.insertNewPathInfo(index, coordinate, paths)
                                } else {
                                    // 올가미
                                }
                            },
                            onDrag = { change, _ ->
                                val newPoint = change.position
                                if (noteController.penType != PenType.Lasso) {
                                    noteController
                                        .updateLatestPath(
                                            offsetToCoordinate(newPoint), paths
                                        )
                                } else {
                                    // 올가미
                                }
                            },
                            onDragEnd = {
                                noteController.getLastPath(paths)
                            }
                        )
                    }

                Canvas(
                    modifier = canvasModifier
                ) {
                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)

                        paths.forEach { pathInfo ->
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
                        restoreToCount(checkPoint)
                    }
                }
            }
        }
    }
)