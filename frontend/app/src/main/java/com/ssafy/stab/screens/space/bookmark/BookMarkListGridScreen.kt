package com.ssafy.stab.screens.space.bookmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.BookmardFolder
import com.ssafy.stab.apis.space.bookmark.BookmardNote
import com.ssafy.stab.apis.space.bookmark.BookmardPage
import com.ssafy.stab.apis.space.bookmark.addBookMark
import com.ssafy.stab.apis.space.bookmark.deleteBookMark
import java.time.format.DateTimeFormatter

@Composable
fun BookMarkListGridScreen(
    folders: List<BookmardFolder>,
    notes: List<BookmardNote>,
    pages: List<BookmardPage>
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
                        FolderItem(folder = folder)
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
                        NoteItem(note = note)
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
                        PageItem(page = page)
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
}

@Composable
fun FolderItem(folder: BookmardFolder) {
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val folderImg = painterResource(id = R.drawable.folder)
    var isLiked by remember{ mutableStateOf( true ) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable { /* 폴더 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(painter = folderImg, contentDescription = "폴더", modifier = Modifier.size(120.dp, 160.dp))
            Image(painter = bookmarkIcon, contentDescription = "즐겨찾기", modifier = Modifier
                .size(48.dp)
                .padding(10.dp)
                .clickable {
                    if (isLiked) {
                        deleteBookMark(folder.folderId)
                    } else {
                        addBookMark(folder.folderId)
                    }
                    isLiked = !isLiked
                }
                .align(Alignment.TopEnd))
        }
        Text(text = folder.title ?: "untitled", fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = folder.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun NoteItem(note: BookmardNote) {
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val notebookImg = painterResource(id = R.drawable.notebook)
    var isLiked by remember{ mutableStateOf( true ) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable { /* 노트 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box{
            Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier.size(120.dp, 160.dp))
            Image(painter = bookmarkIcon, contentDescription = "즐겨찾기", modifier = Modifier
                .size(48.dp)
                .padding(10.dp)
                .clickable {
                    if (isLiked) {
                        deleteBookMark(note.noteId)
                    } else {
                        addBookMark(note.noteId)
                    }
                    isLiked = !isLiked
                }
                .align(Alignment.TopEnd))
        }
        Text(text = note.title, fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = note.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun PageItem(page: BookmardPage) {
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val pageImg = painterResource(id = R.drawable.lined_white_portrait)
    var isLiked by remember{ mutableStateOf( true ) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable { /* 페이지 클릭 핸들러 */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(painter = pageImg, contentDescription = "페이지", modifier = Modifier.size(120.dp, 160.dp))
            Image(painter = bookmarkIcon, contentDescription = "즐겨찾기", modifier = Modifier
                .size(48.dp)
                .padding(10.dp)
                .clickable {
                    if (isLiked) {
                        deleteBookMark(page.pageId)
                    } else {
                        addBookMark(page.pageId)
                    }
                    isLiked = !isLiked
                }
                .align(Alignment.TopEnd))
        }
        Text(text = page.pageId ?: "untitled", fontSize = 16.sp, textAlign = TextAlign.Center)
        Text(text = page.updatedAt.format(DateTimeFormatter.ISO_DATE), fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}
