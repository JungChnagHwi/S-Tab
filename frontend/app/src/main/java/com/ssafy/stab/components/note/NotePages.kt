package com.ssafy.stab.components.note

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.screens.note.NoteViewModel
import com.ssafy.stab.ui.theme.NoteBackground
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.NoteControlViewModel
import com.ssafy.stab.util.note.getTemplate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageList(
    noteViewModel: NoteViewModel,
    noteControlViewModel: NoteControlViewModel,
    onPageChange: (Int) -> Unit,
) {
    val pageList by noteViewModel.pageList.collectAsState()
    val pageCount = pageList.size
    val state = rememberPagerState { pageCount }
    val pageIndex = state.currentPage + 1

    LaunchedEffect(state) {
        snapshotFlow { state.settledPage }.collect { page ->
            onPageChange(page)
        }
    }

    if (pageCount > 0) {
        HorizontalPager(
            state = state
        ) {
            page ->
            Box {
                Page(pageList[page], noteControlViewModel)
                Text(
                    text = "$pageIndex / $pageCount",
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun Page(
    page: PageDetail,
    viewModel: NoteControlViewModel
) {
    val scale by remember { viewModel.scale }
    var offset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(NoteBackground),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = page.direction == 0
        val templateType = page.template
        val backgroundColor = page.color
        val direction = if (isLandscape) Direction.Landscape else Direction.Portrait
        val templateResId = getTemplate(templateType, backgroundColor, direction)

        // 템플릿 이미지 크기
        val context = LocalContext.current
        val imageBitmap = remember {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(context.resources, templateResId, options)
            IntSize(options.outWidth, options.outHeight)
        }
        val templateWidth = imageBitmap.width.toFloat()
        val templateHeight = imageBitmap.height.toFloat()

        // modifier 적용된 템플릿 크기
        var displaySize by remember { mutableStateOf(IntSize(1, 1)) }

        val state = rememberTransformableState { zoomChange, panChange, _ ->
            viewModel.setScale((scale * zoomChange).coerceIn(1f, null))

            val scaledWidth = scale * displaySize.width.toFloat()
            val scaledHeight = scale * displaySize.height.toFloat()

            val canPanHorizontally = scaledWidth > constraints.maxWidth
            val canPanVertically = scaledHeight > constraints.maxHeight

            val extraWidth =
                if (canPanHorizontally) (scaledWidth - constraints.maxWidth) / 2 else 0f

            val extraHeight =
                if (canPanVertically) (scaledHeight - constraints.maxHeight) / 2 else 0f

            offset = Offset(
                x = (offset.x + scale * panChange.x).coerceIn(-extraWidth, extraWidth),
                y = (offset.y + scale * panChange.y).coerceIn(-extraHeight, extraHeight),
            )
        }

        val modifier =
            if (templateWidth * constraints.maxHeight > templateHeight * constraints.maxWidth) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.fillMaxHeight()
            }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state)
                .aspectRatio(templateWidth / templateHeight)

        Template(
            resId = templateResId,
            modifier = modifier
                .onGloballyPositioned { layoutCoordinates ->
                    displaySize = layoutCoordinates.size
                }
        )

        NoteArea(
            page.pageId,
            page.paths,
            modifier,
            viewModel
        )
    }
}