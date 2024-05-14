package com.ssafy.stab.screens.space.bookmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.BookmardFolder
import com.ssafy.stab.apis.space.bookmark.BookmardNote
import com.ssafy.stab.apis.space.bookmark.BookmardPage

@Composable
fun BookMarkListGridScreen(
    folders: List<BookmardFolder>,
    notes: List<BookmardNote>,
    pages: List<BookmardPage>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(text = "---폴더---", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
        }

        items(folders.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { folder ->
                    Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                        FolderItem(folder = folder)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f).padding(8.dp))
                }
            }
        }

        item {
            Text(text = "---노트---", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
        }

        items(notes.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { note ->
                    Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                        NoteItem(note = note)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f).padding(8.dp))
                }
            }
        }

        item {
            Text(text = "---페이지---", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
        }

        items(pages.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                rowItems.forEach { page ->
                    Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                        PageItem(page = page)
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f).padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun FolderItem(folder: BookmardFolder) {
    val folderImg = painterResource(id = R.drawable.folder)
    Column(
        modifier = Modifier
            .clickable { /* 폴더 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = folderImg, contentDescription = "폴더", modifier = Modifier.size(120.dp, 160.dp))
        Text(text = folder.title ?: "untitled", fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun NoteItem(note: BookmardNote) {
    val notebookImg = painterResource(id = R.drawable.notebook)
    Column(
        modifier = Modifier
            .clickable { /* 노트 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier.size(120.dp, 160.dp))
        Text(text = note.title, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun PageItem(page: BookmardPage) {
    val pageImg = painterResource(id = R.drawable.lined_white_portrait)
    Column(
        modifier = Modifier
            .clickable { /* 페이지 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = pageImg, contentDescription = "페이지", modifier = Modifier.size(120.dp, 160.dp))
        Text(text = page.pageId ?: "untitled", fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
