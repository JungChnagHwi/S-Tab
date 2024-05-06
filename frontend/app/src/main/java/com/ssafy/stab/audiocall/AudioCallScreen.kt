package com.ssafy.stab.audiocall

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

// 음성 통화를 위한 임시 ui
@Composable
fun AudioCallScreen(audioCallViewModel: AudioCallViewModel = viewModel()) {
    val context = LocalContext.current
    var roomName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    // 권한 상태를 확인하고 처리하는 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            // 권한이 허용됐을 때 필요한 기능 실행
            if (isGranted) {
                audioCallViewModel.connectToServerAndJoinRoom(roomName, userName)
            } else {
                // 권한 거부 처리
            }
        }
    )

    // 컴포저블이 화면에 표시될 때 권한 자동 확인
    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column {
        TextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") }
        )
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") }
        )
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                audioCallViewModel.connectToServerAndJoinRoom(roomName, userName)
            } else {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Text(text = "Connect and Join Room")
        }
    }
}