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
        title = { Text("Audio Permission Needed") },
        text = { Text("This application needs audio recording permission to proceed.") },
        confirmButton = {
            Button(onClick = {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDialogDismiss()
            }) {
                Text("Deny")
            }
        }
    )
}