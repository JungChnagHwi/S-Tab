package com.ssafy.stab.screens.space

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ShareSpace(onNavigate: (String) -> Unit) {
    Column {
        Text(text = "공유 스페이스")
        Row {
            Button(onClick = { onNavigate("personal-space") }) {
                Text(text = "개인 스페이스")
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
            Button(onClick = { onNavigate("share-space") }) {
                Text(text = "공유 노트")
            }
        }
    }
}