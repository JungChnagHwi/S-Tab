package com.ssafy.stab.util.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ssafy.stab.util.note.data.PenType

@Composable
fun NoteArea(
    noteController: NoteController,
    modifier: Modifier = Modifier.fillMaxSize(),
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) = AndroidView(
    modifier = modifier,
    factory = {
        ComposeView(it).apply {
            setContent {
                LaunchedEffect(noteController) {
                    noteController.trackHistory(this, trackHistory)
                }

                val canvasModifier = modifier
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val coordinate = offsetToCoordinate(offset)
                                noteController.insertNewPathInfo(coordinate)
                                noteController.updateLatestPath(coordinate)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val coordinate = offsetToCoordinate(offset)
                                if (noteController.penType != PenType.Lasso) {
                                    noteController.insertNewPathInfo(coordinate)
                                } else {
                                    // 올가미
                                }
                            }
                        ) { change, _ ->
                            val newPoint = change.position
                            if (noteController.penType != PenType.Lasso) {
                                noteController.updateLatestPath(offsetToCoordinate(newPoint))
                            } else {
                                // 올가미
                            }
                        }
                    }

                Canvas(modifier = canvasModifier
                ) {
                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)

                        noteController.pathList.forEach { pathInfo ->
                            if (pathInfo.penType == PenType.Pen) {
                                drawPath(
                                    path = createPath(pathInfo.coordinates),
                                    color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                                    style = Stroke(
                                        width = pathInfo.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            } else if (pathInfo.penType == PenType.Highlighter) {
                                drawPath(
                                    path = createPath(pathInfo.coordinates),
                                    color = Color(color = ("40" + pathInfo.color).toLong(16)),
                                    style = Stroke(
                                        width = pathInfo.strokeWidth,
                                        cap = StrokeCap.Square,
                                        join = StrokeJoin.Round
                                    )
                                )
                            } else {
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
                        restoreToCount(checkPoint)
                    }
                }
            }
        }
    },
)