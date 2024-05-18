package com.ssafy.stab.screens.space.bookmark

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.BookmardFolder
import com.ssafy.stab.apis.space.bookmark.BookmardNote
import com.ssafy.stab.apis.space.bookmark.BookmardPage
import com.ssafy.stab.apis.space.bookmark.addBookMark
import com.ssafy.stab.apis.space.bookmark.deleteBookMark
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.util.note.getTemplate
import com.ssafy.stab.apis.space.bookmark.getFolderPath
import com.ssafy.stab.screens.space.NoteListSpace
import java.time.format.DateTimeFormatter

@Composable
fun BookMarkListGridScreen(
    folders: List<BookmardFolder>,
    notes: List<BookmardNote>,
    pages: List<BookmardPage>,
    navController: NavController
) {
    val sortedFolders = folders.sortedByDescending { it.updatedAt }
    val sortedNotes = notes.sortedByDescending { it.updatedAt }
    val sortedPages = pages.sortedByDescending { it.updatedAt }
    var selectedFolder by remember { mutableStateOf("") }
    val selectedPathId = remember { mutableStateListOf<String>() }
    val selectedPathTitle = remember { mutableStateListOf<String>() }

    fun selectFolder(selectedFolderId: String, pathIdList: List<String>, pathTitleList: List<String>) {
        selectedFolder = selectedFolderId
        selectedPathId.clear()
        selectedPathTitle.clear()
        selectedPathId.addAll(pathIdList)
        selectedPathTitle.addAll(pathTitleList)
    }

    fun changeFolder(folderId: String) {
        selectedFolder = folderId
        Log.d("Selected Folder Changed", selectedFolder)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedFolder != "" ) {
            PathDisplay(selectedPathId, selectedPathTitle, ::changeFolder)
            NoteListSpace(selectedFolder){}
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp)
            ) {
                item {
                    Text(
                        text = "폴더(${sortedFolders.size})",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5584FD),
                        modifier = Modifier.padding(20.dp, 0.dp)
                    )
                }

                items(sortedFolders.chunked(5)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        rowItems.forEach { folder ->
                            Box(modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)) {
                                FolderItem(folder = folder, selectFolder = { folderId, pathIdList, pathTitleList ->
                                    selectFolder(folderId, pathIdList, pathTitleList)
                                })
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
                    Text(
                        text = "노트(${sortedNotes.size})",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5584FD),
                        modifier = Modifier.padding(20.dp, 0.dp)
                    )
                }

                items(sortedNotes.chunked(5)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        rowItems.forEach { note ->
                            Box(modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)) {
                                NoteItem(note = note, navController)
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
                    Text(
                        text = "페이지(${sortedPages.size})",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5584FD),
                        modifier = Modifier.padding(20.dp, 0.dp)
                    )
                }

                items(sortedPages.chunked(5)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        rowItems.forEach { page ->
                            Box(modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)) {
                                PageItem(page = page, navController)
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
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun FolderItem(
    folder: BookmardFolder,
    selectFolder: (String, List<String>, List<String>) -> Unit
) {
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val folderImg = painterResource(id = R.drawable.folder)
    var isLiked by remember { mutableStateOf(true) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable {
                getFolderPath(folder.rootFolderId, folder.folderId) { res ->
                    val folderIds = res.folders.map { it.folderId }
                    val titles = res.folders.map { it.title ?: "Untitled" }
                    Log.d("리스트 줘봐", res.folders.joinToString(","))
                    Log.d("아이디 리스트", folderIds.joinToString(","))
                    Log.d("타이틀 리스트", titles.joinToString(","))
                    selectFolder(folder.folderId, folderIds, titles)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(painter = folderImg, contentDescription = "폴더", modifier = Modifier.size(102.dp, 136.dp))
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
        Text(
            text = folder.title ?: "untitled",
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
        Text(
            text = folder.updatedAt.format(DateTimeFormatter.ISO_DATE),
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
    }
}

@Composable
fun NoteItem(note: BookmardNote, navController: NavController) {
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val notebookImg = painterResource(id = R.drawable.notebook)
    var isLiked by remember { mutableStateOf(true) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable {
                navController.navigate("")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier
                .size(102.dp, 136.dp)
                .clickable {
                    navController.navigate("note/${note.noteId}/${note.spaceId}/p")
                })
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
        Text(
            text = note.title,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
        Text(
            text = note.updatedAt.format(DateTimeFormatter.ISO_DATE),
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
    }
}

@Composable
fun PageItem(page: BookmardPage, navController: NavController) {
    val staronImg = painterResource(id = R.drawable.bookmark_on)
    val staroffImg = painterResource(id = R.drawable.bookmark_off_white)
    val pageImg = painterResource(id = getTemplate(
            page.template,
            page.color,
            if (page.direction == 0) Direction.Landscape else Direction.Portrait
        ))
    var isLiked by remember { mutableStateOf(true) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier
            .clickable {},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(painter = pageImg, contentDescription = "페이지", modifier = Modifier.size(102.dp, 136.dp).clickable { navController.navigate("note/${page.noteId}/${page.spaceId}/${page.pageId}") })
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
        Text(
            text = page.pageId ?: "untitled",
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
        Text(
            text = page.updatedAt.format(DateTimeFormatter.ISO_DATE),
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp) // 가로 영역 제한
        )
    }
}

@Composable
fun PathDisplay(pathIds: List<String>, pathTitles: List<String>, changeFolder: (String) -> Unit) {
    val annotatedString = buildAnnotatedString {
        pathTitles.forEachIndexed { index, title ->
            if (index > 0) {
                append("    --    ") // 루트 사이 여백을 ' > ' 문자로 구분
            }
            pushStringAnnotation(tag = "ID", annotation = pathIds[index])
            withStyle(style = SpanStyle(color = Color.Blue, fontSize = 20.sp)) {
                append(title)
            }
            pop()
        }
    }

    Row(modifier = Modifier.padding(start = 20.dp)) { // 왼쪽에 20dp의 여백
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "ID", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        Log.d("Selected Path ID", annotation.item)
                        changeFolder(annotation.item)
                    }
            }
        )
    }
}
