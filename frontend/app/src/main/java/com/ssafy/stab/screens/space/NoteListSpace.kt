package com.ssafy.stab.screens.space

import NoteListViewModelFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.modals.CreateNoteModal
import com.ssafy.stab.screens.space.personal.LocalNowFolderTitle
import com.ssafy.stab.screens.space.personal.LocalPrevFolderTitle
import java.time.format.DateTimeFormatter

@Composable
fun NoteListSpace(nowId: String, navController: NavController) {

    val folderIdState = remember { mutableStateOf(nowId) }
    val listImg = painterResource(id = R.drawable.list)
    val isNameSort = remember { mutableStateOf(false) }


    Column {
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                    .clickable { isNameSort.value = !isNameSort.value }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (!isNameSort.value) Color(0xFF7A99D5) else Color(0xFFC3CCDE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "날짜",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isNameSort.value) Color(0xFF7A99D5) else Color(0xFFC3CCDE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "이름",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            Image(painter = listImg, contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp))
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(15.dp))
            ListGridScreen(folderIdState.value, navController) { newFolderId ->
                folderIdState.value = newFolderId
            }
        }
    }
}


@Composable
fun ListGridScreen(initfolderId: String, navController: NavController, onFolderChange: (String) -> Unit) {
    var folderId by remember { mutableStateOf(initfolderId) }
    val showNoteModal = remember { mutableStateOf(false) }
    val showFolderModal = remember { mutableStateOf(false) }
    val showCreateOptions = remember { mutableStateOf(false) }

    val viewModel: NoteListViewModel = viewModel(factory = NoteListViewModelFactory(folderId))
    val combinedList by viewModel.combinedList.collectAsState()

    val createnoteImg = painterResource(id = R.drawable.createnote)

    if (showNoteModal.value) {
        Dialog(onDismissRequest = { showNoteModal.value = false }) {
            val closeModal = { showNoteModal.value = false}
            Box(
                modifier = Modifier
                    .width(1000.dp)
                    .height(800.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
            ) {
                CreateNoteModal(closeModal, viewModel = viewModel)
            }
        }
    }

    if (showFolderModal.value) {
        Dialog(onDismissRequest = { showFolderModal.value = false }) {
            CreateFolderModal(
                closeModal = { showFolderModal.value = false },
                viewModel = viewModel,
            )
        }
    }

    LazyColumn {
        item {
            Box(contentAlignment = Alignment.CenterStart) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 5.dp, 25.dp, 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = createnoteImg,
                            contentDescription = "새 노트 만들기",
                            modifier = Modifier
                                .width(120.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(20))
                                .clickable { showCreateOptions.value = !showCreateOptions.value }
                        )
                        Text(text = "새로 만들기")
                    }

                    combinedList.take(4).forEach { item ->
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            when (item) {
                                is Folder -> FolderItem(folder = item, viewModel = viewModel, onFolderChange)
                                is Note -> NoteItem(note = item, viewModel, navController)
                            }
                        }
                    }

                    repeat(4 - combinedList.take(4).size) {
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp))
                    }
                }

                if (showCreateOptions.value) {
                    Column(
                        modifier = Modifier
                            .padding(top = 70.dp, start = 120.dp)
                            .height(100.dp)
                            .width(100.dp)
                            .background(
                                color = Color(0XFFC3CCDE),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("폴더 생성", modifier = Modifier.clickable {
                            showCreateOptions.value = false
                            showFolderModal.value = true
                        })
                        Spacer(modifier = Modifier.height(15.dp))
                        Text("노트 생성", modifier = Modifier.clickable {
                            showCreateOptions.value = false
                            showNoteModal.value = true
                        })
                    }
                }
            }
        }
        combinedList.drop(4).chunked(5).forEach { rowItems ->
            item {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    rowItems.forEach { item ->
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            when (item) {
                                is Folder -> FolderItem(folder = item, viewModel, onFolderChange)
                                is Note -> NoteItem(note = item, viewModel, navController)
                            }
                        }
                    }
                    // Fill remaining space
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

@Composable
fun FolderItem(folder: Folder, viewModel: NoteListViewModel, onFolderChange: (String) -> Unit) {
    val folderImg = painterResource(id = R.drawable.folder)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val nowFolderTitle = LocalNowFolderTitle.current
    val prevFolderTitle = LocalPrevFolderTitle.current

    var isLiked by remember{ mutableStateOf( folder.isLiked ) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Column(
        modifier = Modifier.clickable {
            viewModel.updateFolderId(folder.folderId)
            onFolderChange(folder.folderId)
            prevFolderTitle.value = nowFolderTitle.value
            nowFolderTitle.value = folder.title
                                      },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(painter = folderImg, contentDescription = "폴더", modifier = Modifier.size(120.dp, 160.dp))
            Image(painter = bookmarkIcon, contentDescription = "즐겨찾기", modifier = Modifier
                .size(48.dp)
                .padding(10.dp)
                .clickable { isLiked = !isLiked }
                .align(Alignment.TopEnd))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = folder.title)
            Spacer(modifier = Modifier.width(3.dp))
            Image(painter = modiImg, contentDescription = null, modifier = Modifier
                .height(20.dp)
                .width(20.dp))
        }
        Text(text = folder.updatedAt.format(dateFormatter))
    }
}

@Composable
fun NoteItem(note: Note, viewModel: NoteListViewModel, navController: NavController) {
    val notebookImg = painterResource(id = R.drawable.notebook)
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)

    var isLiked by remember{ mutableStateOf( note.isLiked ) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg


    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Column(
        modifier = Modifier.clickable { navController.navigate("personal-note") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier.size(120.dp, 160.dp))
            Image(painter = bookmarkIcon, contentDescription = "즐겨찾기", modifier = Modifier
                .size(48.dp)
                .padding(10.dp)
                .clickable { isLiked = !isLiked }
                .align(Alignment.TopEnd))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = note.title)
            Spacer(modifier = Modifier.width(3.dp))
            Image(painter = modiImg, contentDescription = null, modifier = Modifier
                .height(20.dp)
                .width(20.dp))
        }
        Text(text = note.updatedAt.format(dateFormatter))
    }
}