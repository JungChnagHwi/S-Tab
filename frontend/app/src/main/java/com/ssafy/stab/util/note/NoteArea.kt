package com.ssafy.stab.util.note

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.util.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Composable
fun NoteArea(
    currentPageId: String,
    paths: SnapshotStateList<PathInfo>?,
    modifier: Modifier,
    viewModel: NoteControlViewModel,
) {
    val context = LocalContext.current

    // 이미지 비트맵 상태 관리
    val imageBitmapMap = remember { mutableStateMapOf<String, ImageBitmap?>() }

    val currentImageList by rememberUpdatedState(newValue = viewModel.imageList.filter { it.pageId == currentPageId })
    // 이미지 로드
    LaunchedEffect(currentImageList) {
        currentImageList.forEach { pageImageInfo ->
            if (pageImageInfo.imageInfo.url !in imageBitmapMap) {
                val bitmap = loadImageBitmap(context, pageImageInfo.imageInfo.url)
                imageBitmapMap[pageImageInfo.imageInfo.url] = bitmap
            }
        }
    }

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
                            if (viewModel.penType == PenType.Image && viewModel.currentImageUrl.isNotEmpty()) {
                                val x = initialOffset.x
                                val y = initialOffset.y
                                viewModel.addImageToPage(currentPageId, x, y)
                                viewModel.setImageUrl("")
                                viewModel.changePenType(PenType.Pen)
                            } else if (viewModel.penType != PenType.Lasso) {
                                val coordinate = offsetToCoordinate(initialOffset)
                                viewModel.insertNewPathInfo(currentPageId, coordinate)
                            } else {
                                // 올가미
                            }
                            do {
                                val event = awaitPointerEvent(PointerEventPass.Main)
                                event.changes.forEach { change ->
                                    val coordinate = offsetToCoordinate(change.position)
                                    if (viewModel.penType != PenType.Lasso && viewModel.penType != PenType.Image) {
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

                if (viewModel.imageList.isNotEmpty()) {
                    viewModel.imageList.forEach { pageImageInfo ->
                        if (pageImageInfo.pageId == currentPageId) {
                            val imageBitmap = imageBitmapMap[pageImageInfo.imageInfo.url]
                            imageBitmap?.let {
                                drawImage(it, Offset(pageImageInfo.imageInfo.x, pageImageInfo.imageInfo.y))
                                Log.d("image", "image")
                            }
                        }
                    }
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

suspend fun loadImageBitmap(context: Context, url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val result = (loader.execute(request) as SuccessResult).drawable
            result.toBitmap().asImageBitmap()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}