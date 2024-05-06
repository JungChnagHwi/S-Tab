package com.ssafy.stab.webrtc.audiocall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ssafy.stab.webrtc.fragments.PermissionsDialog
import com.ssafy.stab.webrtc.utils.PermissionManager

// session에 접속하는 ui
@Composable
fun AudioCallScreen(viewModel: AudioCallViewModel) {
    val context = LocalContext.current
    val sessionId by viewModel.sessionId
    val participantName by viewModel.participantName
//    val serverUrl by viewModel.serverUrl
    val isConnected by viewModel.isConnected.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 권한 상태 확인
    val permissionsGranted = remember { mutableStateOf(PermissionManager.arePermissionsGranted(context)) }
    val showPermissionDialog = remember { mutableStateOf(false) }

    if (!permissionsGranted.value) {
        PermissionsDialog(
            onPermissionGranted = {
                permissionsGranted.value = true
                viewModel.buttonPressed(context)
            },
            onPermissionDenied = {
                permissionsGranted.value = false
                viewModel.onError("Audio permission is denied")
            },
            onDialogDismiss = {
                showPermissionDialog.value = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = sessionId,
            onValueChange = { viewModel.sessionId.value = it },
            label = { Text("Session ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = participantName,
            onValueChange = { viewModel.participantName.value = it },
            label = { Text("Participant Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
//        OutlinedTextField(
//            value = serverUrl,
//            onValueChange = { viewModel.serverUrl.value = it },
//            label = { Text("Server URL") },
//            modifier = Modifier.fillMaxWidth()
//        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { viewModel.buttonPressed(context) },
            enabled = !isConnected
        ) {
            Text("Join Session")
        }
        if (isConnected) {
            ParticipantList(viewModel.participants.value)
        }
        if (errorMessage.isNotEmpty()) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }
    }
}
