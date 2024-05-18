package com.ssafy.stab.screens.space

import NoteListViewModelFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.addBookMark
import com.ssafy.stab.apis.space.bookmark.deleteBookMark
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.folder.deleteFolder
import com.ssafy.stab.apis.space.note.deleteNote
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.modals.CreateNoteModal
import com.ssafy.stab.modals.FileEditModal
import com.ssafy.stab.screens.space.personal.*
import com.ssafy.stab.util.SocketManager
import java.time.format.DateTimeFormatter

@Composable
fun NoteListSpace(nowId: String, onNote: (String) -> Unit) {
    val folderIdState = rememberUpdatedState(nowId)
    val glassImg = painterResource(id = R.drawable.glass)

    Column {
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(color = Color(0xFFDCE3F1))
                    .width(200.dp)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = glassImg, contentDescription = null)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "검색", fontFamily = FontFamily.Default)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            Spacer(modifier = Modifier.width(15.dp))
            ListGridScreen(folderIdState.value, onNote)
        }
    }
}

@Composable
fun ListGridScreen(
    initFolderId: String,
    onNote: (String) -> Unit
) {
    val selectedFileId = LocalSelectedFileId.current
    val selectedFileTitle = LocalSelectedFileTitle.current
    val folderId by remember(initFolderId) { mutableStateOf(initFolderId) }
    val showNoteModal = remember { mutableStateOf(false) }
    val showFolderModal = remember { mutableStateOf(false) }
    val showEditDeleteOptions = remember { mutableStateOf(false) }
    val showCreateOptions = remember { mutableStateOf(false) }
    val showEditModal = remember { mutableStateOf(false) }

    val viewModel: NoteListViewModel = viewModel(
        key = initFolderId,
        factory = NoteListViewModelFactory(folderId))
    val combinedList by viewModel.combinedList.collectAsState()

    // 소켓에 노트 리스트 뷰모델 데이터 설정
    val socketManager = SocketManager.getInstance()
    LaunchedEffect(viewModel) {
        socketManager.setViewModel(viewModel)
    }

    val createNoteImg = painterResource(id = R.drawable.createnote)

    fun executeDelete() {
        if (selectedFileId.value[0] == 'f') {
            deleteFolder(selectedFileId.value)
            viewModel.deleteFolder(selectedFileId.value)
            PreferencesUtil.getShareSpaceState()
                ?.let { socketManager.updateSpace(it, "FolderDeleted", selectedFileId.value) }
        } else if (selectedFileId.value[0] == 'n') {
            deleteNote(selectedFileId.value)
            viewModel.deleteNote(selectedFileId.value)
            PreferencesUtil.getShareSpaceState()
                ?.let { socketManager.updateSpace(it, "NoteDeleted", selectedFileId.value) }
        }
        showEditDeleteOptions.value = false
    }

    if (showNoteModal.value) {
        Dialog(onDismissRequest = { showNoteModal.value = false }) {
            val closeModal = { showNoteModal.value = false }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
            ) {
                CreateFolderModal(
                    closeModal = { showFolderModal.value = false },
                    viewModel = viewModel,
                )
            }
        }
    }

    if (showEditModal.value) {
        Dialog(onDismissRequest = { showEditModal.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
            ) {
                FileEditModal(
                    closeModal = { showEditModal.value = false },
                    viewModel = viewModel,
                    fileId = selectedFileId.value,
                    fileTitle = selectedFileTitle.value
                )
            }
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
                            painter = createNoteImg,
                            contentDescription = "새 노트 만들기",
                            modifier = Modifier
                                .width(102.dp)
                                .height(136.dp)
                                .clip(RoundedCornerShape(20))
                                .clickable { showCreateOptions.value = !showCreateOptions.value }
                        )
                        Text(text = "새로 만들기", fontFamily = FontFamily.Default)
                    }

                    combinedList.take(4).forEach { item ->
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)) {
                            when (item) {
                                is Folder -> FolderItem(folder = item, viewModel = viewModel,
                                    executeDelete = { executeDelete() }
                                ) { showEditDeleteOptions.value = true }

                                is Note -> NoteItem(note = item, onNote,
                                    executeDelete = { executeDelete() }
                                ) { showEditDeleteOptions.value = true }
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
                        Text("폴더 생성",  fontFamily = FontFamily.Default, modifier = Modifier.clickable {
                            showCreateOptions.value = false
                            showFolderModal.value = true
                        })
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text("노트 생성", fontFamily = FontFamily.Default, modifier = Modifier.clickable {
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
                                is Folder -> FolderItem(folder = item, viewModel,
                                    executeDelete = { executeDelete() }
                                ) { showEditDeleteOptions.value = true }

                                is Note -> NoteItem(note = item, onNote,
                                    executeDelete = { executeDelete() }
                                ) { showEditDeleteOptions.value = true }
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
fun FolderItem(
    folder: Folder,
    viewModel: NoteListViewModel,
    executeDelete: () -> Unit,
    showEditDeleteOptions: () -> Unit
) {
    val folderImg = painterResource(id = R.drawable.folder)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)
    val selectedFileId = LocalSelectedFileId.current
    val selectedFileTitle = LocalSelectedFileTitle.current
    val navigationStackId = LocalNavigationStackId.current
    val navigationStackTitle = LocalNavigationStackTitle.current

    var showOptions by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(folder.isLiked) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    Box(modifier = Modifier.clickable {
        viewModel.updateFolderId(folder.folderId)
        navigationStackId.add(folder.folderId)
        navigationStackTitle.add(folder.title)
    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    selectedFileId.value = folder.folderId
                    selectedFileTitle.value = folder.title
                    showOptions = true
                }
            ) {
                Text(text = folder.title)
                Spacer(modifier = Modifier.width(3.dp))
                Image(painter = modiImg, contentDescription = null, modifier = Modifier
                    .height(20.dp)
                    .width(20.dp))
            }
            Text(text = folder.updatedAt.format(dateFormatter))
        }
        if (showOptions) {
            EditDeleteOptions(
                modifier = Modifier
                    .padding(top = 70.dp, start = 120.dp),
                onEdit = {
                    showOptions = false
                    showEditDeleteOptions()
                },
                onDelete = {
                    showOptions = false
                    executeDelete()
                }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNote: (String) -> Unit,
    executeDelete: () -> Unit,
    showEditDeleteOptions: () -> Unit
) {
    val notebookImg = painterResource(id = R.drawable.notebook)
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)

    val selectedFileId = LocalSelectedFileId.current
    val selectedFileTitle = LocalSelectedFileTitle.current
    var showOptions by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(note.isLiked) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Box(modifier = Modifier.clickable { onNote(note.noteId) }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Image(painter = notebookImg, contentDescription = "노트", modifier = Modifier.size(102.dp, 136.dp))
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    selectedFileId.value = note.noteId
                    selectedFileTitle.value = note.title
                    showOptions = true
                }
            ) {
                Text(text = note.title)
                Spacer(modifier = Modifier.width(3.dp))
                Image(painter = modiImg, contentDescription = null, modifier = Modifier
                    .height(20.dp)
                    .width(20.dp))
            }
            Text(text = note.updatedAt.format(dateFormatter))
        }
        if (showOptions) {
            EditDeleteOptions(
                modifier = Modifier
                    .padding(top = 70.dp, start = 120.dp),
                onEdit = {
                    showOptions = false
                    showEditDeleteOptions()
                },
                onDelete = {
                    showOptions = false
                    executeDelete()
                }
            )
        }
    }
}
@Composable
fun EditDeleteOptions(
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .width(100.dp)
            .background(color = Color(0xFFC3CCDE), shape = RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("수정", fontFamily = FontFamily.Default, modifier = Modifier.clickable {
                onEdit()
            })
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text("삭제", fontFamily = FontFamily.Default, modifier = Modifier.clickable {
                onDelete()
            })
        }
    }
}