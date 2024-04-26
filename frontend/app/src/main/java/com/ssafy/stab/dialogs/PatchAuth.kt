package com.ssafy.stab.dialogs

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
//        title = { Text("인증 필요") },
//        text = { Text("본인 인증이 필요합니다.") }
    )
}