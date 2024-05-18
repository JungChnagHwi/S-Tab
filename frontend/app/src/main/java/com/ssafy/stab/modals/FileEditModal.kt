package com.ssafy.stab.modals

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.folder.deleteFolder
import com.ssafy.stab.apis.space.folder.renameFolder
import com.ssafy.stab.apis.space.note.deleteNote
import com.ssafy.stab.apis.space.note.renameNote
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.util.SocketManager

@Composable
fun FileEditModal(closeModal: () -> Unit, viewModel: NoteListViewModel, fileId: String, fileTitle: String) {
    var fileTitle by remember { mutableStateOf(fileTitle) }
    val folderImg = painterResource(id = R.drawable.folder)
    val noteImg = painterResource(id = R.drawable.notebook)
    val socketManager = SocketManager.getInstance()

    var titleText = "폴더 이름 변경"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFDCE3F1)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        if (fileId[0] == 'f') {
            Image(painter = folderImg, contentDescription = "폴더 이미지")
        } else if (fileId[0] == 'n') {
            Spacer(modifier = Modifier.height(20.dp))
            Image(painter = noteImg, contentDescription = "노트 이미지")
            titleText = "노트 이름 변경"
        }
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = fileTitle,
            onValueChange = { fileTitle = it },
            label = { Text(titleText, fontFamily = FontFamily.Default) },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.6f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            ),
            singleLine = true
        )
        Row(Modifier.padding(10.dp)) {
            Button(
                onClick = { closeModal() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary // 생성 버튼의 글자색 사용
                )
            ) {
                Text(text = "취소", fontFamily = FontFamily.Default)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                if (fileId[0] == 'f') {
                    renameFolder(fileId, fileTitle)
                    viewModel.renameFolder(fileId, fileTitle)
                    Log.d("FolderUpdated", fileId)
                    PreferencesUtil.getShareSpaceState()
                        ?.let { spaceId ->
                            val updatedData = mapOf("folderId" to fileId, "newTitle" to fileTitle)
                            socketManager.updateSpace(spaceId, "FolderUpdated", updatedData)
                        }
                    closeModal()
                } else if (fileId[0] == 'n') {
                    renameNote(fileId, fileTitle)
                    viewModel.renameNote(fileId, fileTitle)
                    Log.d("NoteUpdated", fileId)
                    PreferencesUtil.getShareSpaceState()
                        ?.let { spaceId ->
                            val updatedData = mapOf("noteId" to fileId, "newTitle" to fileTitle)
                            socketManager.updateSpace(spaceId, "NoteUpdated", updatedData)
                        }
                    closeModal()
                }
            }) {
                Text(text = "이름 수정", fontFamily = FontFamily.Default)
            }
        }
    }
}