package com.ssafy.stab.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SideBar(onNavigate: (String) -> Unit) {
    Column {
        Button(onClick = { onNavigate("personal-space") }) {
            Text(text = "개인 스페이스")
        }
        Button(onClick = { onNavigate("share-space") }) {
            Text(text = "공유 스페이스")
        }
        Button(onClick = { onNavigate("book-mark") }) {
            Text(text = "즐겨찾기")
        }
        Button(onClick = { onNavigate("deleted") }) {
            Text(text = "휴지통")
        }
        Button(onClick = { onNavigate("personal-note") }) {
            Text(text = "개인 노트")
        }
        Button(onClick = { onNavigate("share-note") }) {
            Text(text = "공유 노트")
        }
        Button(onClick = { onNavigate("patch-auth") }) {
            Text(text = "회원 정보 수정 / 로그아웃")
        }
    }
}