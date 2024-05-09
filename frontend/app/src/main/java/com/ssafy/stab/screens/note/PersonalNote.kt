package com.ssafy.stab.screens.note

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.components.note.Template
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Coordinate
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.data.note.PathInfo
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.data.note.response.Page
import com.ssafy.stab.data.note.response.Response
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.data.note.response.PageData
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.data.note.response.PageListResponse
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.ui.theme.NoteAreaBackground
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.getTemplate
import com.ssafy.stab.util.note.rememberNoteController
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonalNote(navController: NavController){
    val noteController = rememberNoteController()
    val undoAvailable = remember { mutableStateOf(false) }
    val redoAvailable = remember { mutableStateOf(false) }

    val coordinates: SnapshotStateList<Coordinate> = mutableStateListOf(Coordinate(5f,5f),Coordinate(5f,6f),Coordinate(5f,15f))

    val data = mutableListOf(
        PageData(PageDetail(
            "1",
            BackgroundColor.Yellow, TemplateType.Lined, 0,
            false, "", 0,
            LocalDateTime.parse("2024-05-08 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            mutableListOf(
                PathInfo(PenType.Pen, 10f, "000000", coordinates)
            ),
            mutableListOf(), mutableListOf(), mutableListOf()
        )),
        PageData(PageDetail(
            "1",
            BackgroundColor.Yellow, TemplateType.Lined, 0,
            false, "", 0,
            LocalDateTime.parse("2024-05-08 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            mutableListOf(
                PathInfo(PenType.Pen, 20f, "00FF00", coordinates)
            ),
            mutableListOf(), mutableListOf(), mutableListOf()
        )),
    )
    val response = PageListResponse(data = data)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "뒤로가기")
            }
        }

        Row(
            modifier = Modifier
                .background(Background)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ControlsBar(
                noteController = noteController,
                undoAvailable = undoAvailable,
                redoAvailable = redoAvailable,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OptionsBar(noteController = noteController)
        }

        PageList(response, noteController)

    }
}