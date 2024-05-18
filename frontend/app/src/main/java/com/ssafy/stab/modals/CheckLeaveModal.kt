package com.ssafy.stab.modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CheckLeaveModal(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "공유스페이스를 나가겠습니까?")
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("나가기")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("취소")
                }
            }
        )
    }
}