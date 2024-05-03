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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NoteArea(
    noteController: NoteController,
    modifier: Modifier = Modifier.fillMaxSize(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) = AndroidView(
    factory = {
        ComposeView(it).apply {
            setContent {
                LaunchedEffect(noteController) {
                    noteController.trackHistory(this, trackHistory)
                }
                Canvas(modifier = modifier
                    .background(backgroundColor)
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
                                noteController.insertNewPathInfo(coordinate)
                            }
                        ) { change, _ ->
                            val newPoint = change.position
                            noteController.updateLatestPath(offsetToCoordinate(newPoint))
                        }
                    }
                ) {
                    noteController.pathList.forEach { pathInfo ->
                        drawPath(
                            createPath(pathInfo.coordinates),
                            color = Color(color = ("FF" + pathInfo.color).toLong(16)),
                            style = Stroke(
                                width = pathInfo.thickness,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    },
    modifier = modifier
)