package com.ssafy.stab.modals

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.folder.createFolder
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.util.SocketManager


@Composable
fun CreateFolderModal(closeModal: () -> Unit, viewModel: NoteListViewModel) {
    var folderName by remember { mutableStateOf("제목 없는 폴더") }
    val folderImg = painterResource(id = R.drawable.folder)
    val folderId by viewModel.folderId.collectAsState()
    val socketManager = SocketManager.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFDCE3F1)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(painter = folderImg, contentDescription = "폴더 이미지")
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = folderName,
            onValueChange = {newValue ->
                if (newValue.isEmpty() || newValue.first() != ' ') {
                    folderName = newValue
                }
            },
            label = { Text("폴더 이름", fontFamily = FontFamily.Default) },
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
            Button(onClick = { createFolder(folderId, folderName) { response ->
                viewModel.addFolder(response)
                Log.d("CheckingFolder", response.toString())
                Log.d("ShareSpaceState: ", PreferencesUtil.getShareSpaceState().toString())
                PreferencesUtil.getShareSpaceState()
                    ?.let { socketManager.updateSpace(it, "FolderCreated", response) }
            }
                closeModal()
            }) {
                Text(text = "생성", fontFamily = FontFamily.Default)
            }
        }
    }
}