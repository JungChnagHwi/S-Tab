package com.ssafy.stab.screens.space.deleted

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.trash.TrashFolder
import com.ssafy.stab.apis.space.trash.TrashNote
import com.ssafy.stab.apis.space.trash.TrashPage
import com.ssafy.stab.apis.space.trash.restoreTrash
import java.time.format.DateTimeFormatter

@Composable
fun DeletedListGridScreen(
    folders: List<TrashFolder>,
    notes: List<TrashNote>,
    pages: List<TrashPage>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp)
    ) {
        item {
            Text(text = "폴더(${folders.size})", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5584FD),modifier = Modifier.padding(20.dp, 0.dp))
        }
        items(folders.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { folder ->
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)) {
                        FolderItem(folder, navController)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))
                }
            }
        }

        item {
            Text(text = "노트(${notes.size})", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5584FD),modifier = Modifier.padding(20.dp, 0.dp))
        }

        items(notes.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { note ->
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)) {
                        NoteItem(note, navController)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))
                }
            }
        }

        item {
            Text(text = "페이지(${pages.size})", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5584FD),modifier = Modifier.padding(20.dp, 0.dp))
        }

        items(pages.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { page ->
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)) {
                        PageItem(page, navController)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun FolderItem(folder: TrashFolder, navController: NavController) {
    val folderImg = painterResource(id = R.drawable.folder)
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        RestoreDialog(onDismiss = { showDialog.value = false }, onConfirm = {
            restoreTrash(folder.folderId)
            showDialog.value = false
            navController.navigate("personal-space")
        })
    }

    Column(
        modifier = Modifier
            .clickable { showDialog.value = true },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = folderImg, contentDescription = "폴더", modifier = Modifier.size(102.dp, 136.dp))
        Text(text = folder.title ?: "untitled", fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = folder.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun NoteItem(note: TrashNote, navController: NavController) {
    val notebookImg = painterResource(id = R.drawable.notebook)
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        RestoreDialog(onDismiss = { showDialog.value = false }, onConfirm = {
            restoreTrash(note.noteId)
            showDialog.value = false
            navController.navigate("personal-space")
        })
    }

    Column(
        modifier = Modifier
            .clickable { showDialog.value = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier.size(102.dp, 136.dp))
        Text(text = note.title, fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = note.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun PageItem(page: TrashPage, navController: NavController) {
    val pageImg = painterResource(id = R.drawable.lined_white_portrait)
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        RestoreDialog(onDismiss = { showDialog.value = false }, onConfirm = {
            restoreTrash(page.pageId)
            showDialog.value = false
            navController.navigate("personal-space")
        })
    }

    Column(
        modifier = Modifier
            .clickable { showDialog.value = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = pageImg, contentDescription = "페이지", modifier = Modifier.size(102.dp, 136.dp))
        Text(text = page.pageId ?: "untitled", fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = page.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun RestoreDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "복원하시겠습니까?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("복원")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
