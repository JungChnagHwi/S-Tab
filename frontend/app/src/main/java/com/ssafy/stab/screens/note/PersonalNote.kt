package com.ssafy.stab.screens.note

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.OptionsBar
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.ui.theme.NoteAreaBackground
import com.ssafy.stab.ui.theme.YellowNote
import com.ssafy.stab.util.note.NoteArea
import com.ssafy.stab.util.note.rememberNoteController
import kotlinx.coroutines.launch

suspend fun fetchData(): String? {
    return null
}

@Composable
fun PersonalNote(navController: NavController){
    val noteController = rememberNoteController()
    val undoAvailable = remember { mutableStateOf(false) }
    val redoAvailable = remember { mutableStateOf(false) }
    val template = remember { mutableStateOf<String?>(null) }
    val isLandscape = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(template) {
        coroutineScope.launch {
            val url = fetchData()
            template.value = url
        }
    }

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

        // 필기 공간
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().background(NoteAreaBackground),
            contentAlignment = Alignment.Center
        ) {
            val aspectRatio = if (isLandscape.value) 297f / 210f else 210f / 297f
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
                if (template.value != null) {
                    val painter = rememberAsyncImagePainter(model = template.value)

                    // 템플릿
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 무지
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(YellowNote)
                    )
                }

                NoteArea(
                    noteController = noteController,
                ) { undoCount, redoCount ->
                    undoAvailable.value = undoCount != 0
                    redoAvailable.value = redoCount != 0
                }
            }
        }
    }
}


@Preview
@Composable
fun PersonalNotePreview() {
    PersonalNote(navController = rememberNavController())
}