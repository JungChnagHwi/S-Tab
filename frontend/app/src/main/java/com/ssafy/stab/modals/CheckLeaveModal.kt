package com.ssafy.stab.modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

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
                Text(text = "공유스페이스를 나가겠습니까?" , fontFamily = FontFamily.Default)
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("나가기", fontFamily = FontFamily.Default)
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("취소", fontFamily = FontFamily.Default)
                }
            }
        )
    }
}