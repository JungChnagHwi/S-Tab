package com.ssafy.stab.screens.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.components.note.PageInterfaceBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.NoteControlViewModel

@Composable
fun PersonalNote(
    noteViewModel: NoteViewModel,
    navController: NavController
){
    val noteControlViewModel : NoteControlViewModel = viewModel()

    val undoAvailable by noteControlViewModel.undoAvailable.collectAsState()
    val redoAvailable by noteControlViewModel.redoAvailable.collectAsState()

    val currentPage = remember { mutableIntStateOf(0) }
    val onPageChange = { page: Int -> currentPage.intValue = page }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .height(40.dp)
        ) {
            Button(onClick = {
//                noteViewModel.savePage(noteControlViewModel.pathList)
                navController.popBackStack()
            }) {
                Text(text = "뒤로가기")
            }
            PageInterfaceBar(
                currentPage = currentPage.intValue,
                viewModel = noteViewModel,
            )
        }

        Row(
            modifier = Modifier
                .background(Color.Green)
                .align(Alignment.CenterHorizontally)
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ControlsBar(
                noteControlViewModel,
                undoAvailable,
                redoAvailable,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OptionsBar(noteControlViewModel)
        }

        PageList(noteViewModel, noteControlViewModel, onPageChange)
    }
}