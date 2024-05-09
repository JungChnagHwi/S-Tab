package com.ssafy.stab.screens.note

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.stab.apis.note.fetchPageList
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.components.note.PageInterfaceBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.data.note.response.PageData
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.data.note.response.PageListResponse
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.rememberNoteController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PersonalNote(navController: NavController){
    val noteController = rememberNoteController()
    val undoAvailable = remember { mutableStateOf(false) }
    val redoAvailable = remember { mutableStateOf(false) }

    val coordinates: SnapshotStateList<Coordinate> =
        remember { mutableStateListOf(Coordinate(5f, 5f), Coordinate(5f, 6f), Coordinate(5f, 15f)) }
    val paths1: SnapshotStateList<PathInfo> =
        remember { mutableStateListOf(PathInfo(PenType.Pen, 10f, "000000", coordinates)) }
    val paths2: SnapshotStateList<PathInfo> =
        remember { mutableStateListOf(PathInfo(PenType.Highlighter, 20f, "00FF00", coordinates)) }

    val data = mutableListOf(
        PageData(PageDetail(
            "1",
            BackgroundColor.Yellow, TemplateType.Lined, 0,
            false, "", 0,
            LocalDateTime.parse("2024-05-08 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            paths1,
            mutableListOf(), mutableListOf(), mutableListOf()
        )),
        PageData(PageDetail(
            "1",
            BackgroundColor.Yellow, TemplateType.Lined, 0,
            false, "", 0,
            LocalDateTime.parse("2024-05-08 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            paths2,
            mutableListOf(), mutableListOf(), mutableListOf()
        )),
    )

    val response = remember { mutableStateOf<PageListResponse?>(null) }

    response.value = PageListResponse(data = data)
//    fetchPageList() {
//        response.value = it
//    }

    val currentPageIndex = remember { mutableIntStateOf(0) }
    val onPageChange = { index: Int -> currentPageIndex.intValue = index }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "뒤로가기")
            }
        }

        PageInterfaceBar(
            currentPage = currentPageIndex.intValue,
            pageList = response.value!!.data,
            noteController = noteController
        )

        Row(
            modifier = Modifier
                .background(Background)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (response.value != null) {
                ControlsBar(
                    response.value!!.data,
                    noteController,
                    undoAvailable,
                    redoAvailable,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            OptionsBar(noteController = noteController)
        }

        if (response.value != null) {
            PageList(
                response.value!!, noteController, onPageChange
            ) { undoCount, redoCount ->
                undoAvailable.value = undoCount != 0
                redoAvailable.value = redoCount != 0
            }
        }

    }
}