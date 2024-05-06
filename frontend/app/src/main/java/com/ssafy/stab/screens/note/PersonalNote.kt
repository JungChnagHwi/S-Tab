package com.ssafy.stab.screens.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.data.PenType
import com.ssafy.stab.util.note.rememberNoteController

@Composable
fun PersonalNote(navController: NavController){
    val noteController = rememberNoteController()
    val undoAvailable = remember { mutableStateOf(false) }
    val redoAvailable = remember { mutableStateOf(false) }

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
            OptionsBar(noteController = noteController)
        }

        NoteArea(
            noteController = noteController,
        ) {
            undoCount, redoCount ->
            undoAvailable.value = undoCount != 0
            redoAvailable.value = redoCount != 0
        }

    }

}


@Preview
@Composable
fun PersonalNotePreview() {
    PersonalNote(navController = rememberNavController())
}