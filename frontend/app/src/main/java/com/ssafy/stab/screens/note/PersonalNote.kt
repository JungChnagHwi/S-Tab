package com.ssafy.stab.screens.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.rememberNoteController

@Composable
fun PersonalNote(navController: NavController){
    val noteController = rememberNoteController()

    Box {
        Column {
            Row {
                Button(onClick = { navController.popBackStack() }) {
                    Text(text = "뒤로가기")
                }
            }
            NoteArea(
                noteController = noteController,
            )
        }
    }

}


@Preview
@Composable
fun PersonalNotePreview() {
    PersonalNote(navController = rememberNavController())
}