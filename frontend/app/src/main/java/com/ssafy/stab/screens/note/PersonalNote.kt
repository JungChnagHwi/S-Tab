package com.ssafy.stab.screens.note

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.components.note.PageInterfaceBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.rememberNoteController

@Composable
fun PersonalNote(
    viewModel: NoteViewModel,
    navController: NavController
){
    val pageList by viewModel.pageList.collectAsState()

    val noteController = rememberNoteController()
    val undoAvailable = remember { mutableStateOf(false) }
    val redoAvailable = remember { mutableStateOf(false) }

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
            pageList = pageList,
            noteController = noteController
        )

        Row(
            modifier = Modifier
                .background(Background)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ControlsBar(
                pageList,
                noteController,
                undoAvailable,
                redoAvailable,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OptionsBar(noteController = noteController)
        }

        PageList(
            pageList, noteController, onPageChange
        ) { undoCount, redoCount ->
            undoAvailable.value = undoCount != 0
            redoAvailable.value = redoCount != 0
        }
    }
}