package com.ssafy.stab.screens.space

import NoteListViewModelFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.addBookMark
import com.ssafy.stab.apis.space.bookmark.deleteBookMark
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.folder.deleteFolder
import com.ssafy.stab.apis.space.folder.searchFile
import com.ssafy.stab.apis.space.note.deleteNote
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.modals.CreateNoteModal
import com.ssafy.stab.modals.FileEditModal
import com.ssafy.stab.screens.space.bookmark.PageItem
import com.ssafy.stab.screens.space.personal.*
import com.ssafy.stab.util.SocketManager
import java.time.format.DateTimeFormatter


@Composable
fun NoteListSpace(nowId: String, viewModel:NoteListViewModel, onNote: (String) -> Unit) {
    val folderIdState = rememberUpdatedState(nowId)
    val glassImg = painterResource(id = R.drawable.glass)
    var searchText by remember { mutableStateOf("") }
    val searchFolders = remember { mutableStateListOf<Folder>() }
    val searchNotes = remember { mutableStateListOf<Note>() }
    val isSearching by remember { derivedStateOf { searchText.isNotBlank() && (searchFolders.isNotEmpty() || searchNotes.isNotEmpty()) } }

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
                    .width(300.dp)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = glassImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        searchFile(
                            PreferencesUtil.getShareSpaceState().toString(),
                            searchText,
                            {res ->
                                if (res != null) {
                                    Log.d("검색", searchText)
                                    Log.d("폴더", res.joinToString(","))
                                    searchFolders.addAll(res)
                                }
                            },
                            {res ->
                                if (res != null) {
                                    Log.d("노트", res.joinToString(","))
                                    searchNotes.addAll(res)
                                }
                            }
                        )
                    },
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp) // 높이를 조절하여 더 얇게 만듦
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "검색",
                                    style = TextStyle(color = Color.Gray, fontFamily = FontFamily.Default)
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Default,
                        color = Color.Black
                    ),
                    singleLine = true,
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            Spacer(modifier = Modifier.width(15.dp))
            if (isSearching) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchResultDisplay(searchFolders, searchNotes, viewModel, onNote)
                }
            } else {
                ListGridScreen(folderIdState.value, viewModel, onNote)
            }
        }
    }
}

@Composable
fun SearchResultDisplay(folders: List<Folder>, notes: List<Note>, viewModel: NoteListViewModel, onNote: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp)
    ) {
        item {
            Text(
                text = "폴더(${folders.size})",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5584FD),
                modifier = Modifier.padding(20.dp, 0.dp)
            )
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
                        FolderItem(folder = folder, viewModel, {}, {})
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
                text = "노트(${notes.size})",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5584FD),
                modifier = Modifier.padding(20.dp, 0.dp)
            )
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
                        NoteItem(note = note, viewModel, onNote, {}, {})
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
fun ListGridScreen(
    initFolderId: String,
    viewModel: NoteListViewModel,
    onNote: (String) -> Unit
) {
    val selectedFileId = LocalSelectedFileId.current
    val selectedFileTitle = LocalSelectedFileTitle.current
    val folderId by remember(initFolderId) { mutableStateOf(initFolderId) }
    val showNoteModal = remember { mutableStateOf(false) }
    val showFolderModal = remember { mutableStateOf(false) }
    val showCreateOptions = remember { mutableStateOf(false) }
    val showEditModal = remember { mutableStateOf(false) }


    val combinedList by viewModel.combinedList.collectAsState()
    // 소켓에 노트 리스트 뷰모델 데이터 설정
    val socketManager = SocketManager.getInstance()

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
    }

    if (showNoteModal.value) {
        Dialog(onDismissRequest = { showNoteModal.value = false }) {
            val closeModal = { showNoteModal.value = false }
            Box(
                modifier = Modifier
                    .width(1000.dp)
                    .height(700.dp) // 690으로 하고 나머지 비율을 고칠지 고민중
                    .clip(RoundedCornerShape(15.dp))
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
                    .clip(RoundedCornerShape(15.dp))
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { viewModel.closeAllOptions() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
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
                                    .clickable {
                                        showCreateOptions.value = !showCreateOptions.value
                                    }
                            )
                            Text(text = "새로 만들기", fontFamily = FontFamily.Default)
                        }

                        combinedList.take(4).forEach { item ->
                            Box(modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)) {
                                when (item) {
                                    is Folder -> FolderItem(folder = item, viewModel = viewModel,
                                        executeDelete = { executeDelete() },
                                        showEditModal = { showEditModal.value = true }
                                    )

                                    is Note -> NoteItem(note = item, viewModel, onNote,
                                        executeDelete = { executeDelete() },
                                        showEditModal = { showEditModal.value = true }
                                    )
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
                                        executeDelete = { executeDelete() },
                                        showEditModal = { showEditModal.value = true }
                                    )

                                    is Note -> NoteItem(note = item, viewModel, onNote,
                                        executeDelete = { executeDelete() },
                                        showEditModal = { showEditModal.value = true }
                                    )
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
}
@Composable
fun FolderItem(
    folder: Folder,
    viewModel: NoteListViewModel,
    executeDelete: () -> Unit,
    showEditModal: () -> Unit
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

    val showOptions by viewModel.getShowOptionsState(folder.folderId)
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
                    viewModel.closeAllOptions()
                    viewModel.getShowOptionsState(folder.folderId).value = true
                }
            ) {
                Text(
                    text = folder.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(100.dp)
                )
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
                    viewModel.getShowOptionsState(folder.folderId).value = false
                    showEditModal()
                },
                onDelete = {
                    viewModel.getShowOptionsState(folder.folderId).value = false
                    executeDelete()
                }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    viewModel: NoteListViewModel,
    onNote: (String) -> Unit,
    executeDelete: () -> Unit,
    showEditModal: () -> Unit
) {
    val notebookImg = painterResource(id = R.drawable.notebook)
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)

    val showOptions by viewModel.getShowOptionsState(note.noteId)
    var isLiked by remember { mutableStateOf(note.isLiked) }
    val bookmarkIcon = if (isLiked) staronImg else staroffImg

    val selectedFileId = LocalSelectedFileId.current
    val selectedFileTitle = LocalSelectedFileTitle.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Box(modifier = Modifier
        .clickable { onNote(note.noteId) }
    ) {
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
                    viewModel.closeAllOptions()
                    viewModel.getShowOptionsState(note.noteId).value = true
                }
            ) {
                Text(
                    text = note.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(100.dp)
                )
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
                    viewModel.getShowOptionsState(note.noteId).value = false
                    showEditModal()
                },
                onDelete = {
                    viewModel.getShowOptionsState(note.noteId).value = false
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
