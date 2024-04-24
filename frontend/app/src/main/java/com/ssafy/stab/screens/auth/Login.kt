package com.ssafy.stab.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Login(onNavigate: (String) -> Unit){

    Column {
        Text(text = "로그인 페이지")
        Row {
            Button(onClick = { onNavigate("sign-up") }) {
                Text(text = "회원가입 페이지로 가기")
            }
            Button(onClick = { onNavigate("space") }) {
                Text(text = "개인 스페이스로 가기")
            }
        }
    }
}