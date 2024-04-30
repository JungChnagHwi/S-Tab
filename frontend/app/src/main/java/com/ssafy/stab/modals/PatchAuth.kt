package com.ssafy.stab.modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PatchAuth(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("로그아웃")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("회원 정보 수정")
            }
        },
    )
}