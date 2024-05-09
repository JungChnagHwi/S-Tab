package com.ssafy.stab.components.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.data.note.response.PageListResponse
import com.ssafy.stab.ui.theme.NoteAreaBackground
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.NoteController
import com.ssafy.stab.util.note.getTemplate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageList(
    pageList: PageListResponse,
    noteController: NoteController,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) {
    LaunchedEffect(noteController) {
        noteController.trackHistory(this, trackHistory)
    }

    val pageCount = pageList.data.size
    val state = rememberPagerState { pageCount }
    val pageIndex = state.currentPage + 1

    HorizontalPager(
        state = state
    ) {
        page ->
        Box {
            Page(page, pageList.data[page].page, noteController)
            Text(
                text = "$pageIndex / $pageCount",
                Modifier.padding(8.dp).align(Alignment.BottomEnd)
            )
        }
    }

}

@Composable
fun Page(
    index: Int,
    page: PageDetail,
    noteController: NoteController
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(NoteAreaBackground),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = page.direction == 0
        val aspectRatio = if (isLandscape) 297f / 210f else 210f / 297f
        val density = LocalDensity.current

        val maxWidth = with(density) { constraints.maxWidth.toDp() }
        val maxHeight = with(density) { constraints.maxHeight.toDp() }
        val calculatedWidth = maxHeight * (1 / aspectRatio)

        val modifier = if (calculatedWidth > maxWidth) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.fillMaxHeight()
        }

        // 비율을 맞춘 노트
        Box(
            modifier = modifier.aspectRatio(aspectRatio)
        ) {
            val templateType = page.template
            val backgroundColor = page.color
            val direction = if (isLandscape) Direction.Landscape else Direction.Portrait
            val templateResId = getTemplate(templateType, backgroundColor, direction)

            Template(resId = templateResId, modifier = Modifier.matchParentSize())

            NoteArea(
                index,
                page.paths,
                noteController
            )
        }
    }
}