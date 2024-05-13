package com.ssafy.stab.webrtc.fragments

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun PermissionsDialog(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onDialogDismiss: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
        onDialogDismiss()
    }

    AlertDialog(
        onDismissRequest = { onDialogDismiss() },
        title = { Text("음성 권한이 필요합니다.") },
        text = { Text("그룹 통화 기능을 사용하려면 음성 권한 허락이 필요합니다.") },
        confirmButton = {
            Button(onClick = {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }) {
                Text("허락")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDialogDismiss()
            }) {
                Text("거절")
            }
        }
    )
}