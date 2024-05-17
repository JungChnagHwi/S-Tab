package com.ssafy.stab.modals

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.folder.createFolder
import com.ssafy.stab.apis.space.folder.deleteFolder
import com.ssafy.stab.apis.space.folder.renameFolder
import com.ssafy.stab.apis.space.note.deleteNote
import com.ssafy.stab.apis.space.note.renameNote
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.util.SocketManager

@Composable
fun PatchDeleteModal(closeModal: () -> Unit, viewModel: NoteListViewModel, fileId: String, fileTitle: String) {
    var fileTitle by remember{ mutableStateOf(fileTitle) }
    val folderImg = painterResource(id = R.drawable.folder)
    val noteImg = painterResource(id = R.drawable.notebook)
    val socketManager = SocketManager.getInstance()

    Column(
        modifier = Modifier.padding(10.dp).background(color = Color.White).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (fileId[0]== 'f') {
            Image(painter = folderImg, contentDescription = null)
        } else if (fileId[0] == 'n'){
            Image(painter = noteImg, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = fileTitle,
            onValueChange = { fileTitle = it },
            label = { Text("파일 이름 변경") },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.6f)
        )
        Row(Modifier.padding(10.dp)) {
            Button(
                onClick = {
                if (fileId[0]== 'f') {
                    deleteFolder(fileId)
                    viewModel.deleteFolder(fileId)
                    closeModal()
                } else if (fileId[0] == 'n') {
                    deleteNote(fileId)
                    viewModel.deleteNote(fileId)
                    Log.d("NoteDeleted", fileId)
                    PreferencesUtil.getShareSpaceState()
                        ?.let { socketManager.updateSpace(it, "NoteDeleted", fileId) }
                    closeModal()
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )) {
                Text(text = "파일 삭제")
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                if (fileId[0]== 'f') {
                    renameFolder(fileId, fileTitle)
                    viewModel.renameFolder(fileId, fileTitle)
                    closeModal()
                } else if (fileId[0] == 'n') {
                    renameNote(fileId, fileTitle)
                    viewModel.renameNote(fileId, fileTitle)
                    Log.d("NoteUpdated", fileId)
                    PreferencesUtil.getShareSpaceState()
                        ?.let { spaceId ->
                            val updatedData = mapOf("noteId" to fileId, "newTitle" to fileTitle)
                            socketManager.updateSpace(spaceId, "NoteUpdated", updatedData) }
                    closeModal()
                }
            }) {
                Text(text = "이름 수정")
            }
        }
    }
}

