package com.ssafy.stab.audiocall

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

// 음성 통화를 위한 임시 ui
@Composable
fun AudioCallScreen(audioCallViewModel: AudioCallViewModel = viewModel()) {
    var roomName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    Column {
        TextField(value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") })
        TextField(value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") })
        Button(onClick = {
            // 서버 연결 및 방 참여를 한 번에 처리
            audioCallViewModel.connectToServerAndJoinRoom(roomName, userName)
        }) {
            Text(text = "Connect and Join Room")
        }
    }
}