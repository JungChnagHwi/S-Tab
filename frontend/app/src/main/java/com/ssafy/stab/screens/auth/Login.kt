package com.ssafy.stab.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.stab.apitest.apiTestOne
import com.ssafy.stab.apitest.apiTestWhole
import com.ssafy.stab.modals.UserListModal


@Composable
fun Login(onNavigate: (String) -> Unit){
    val kakaoAuthViewModel : KakaoAuthViewModel = viewModel()
    var chooseId by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    val options = List(100) { it + 1 }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        KakaoLoginView(kakaoAuthViewModel)

        Row {
            Button(onClick = { onNavigate("sign-up") }) {
                Text(text = "회원가입 페이지로 가기")
            }
            Button(onClick = { onNavigate("space") }) {
                Text(text = "개인 스페이스로 가기")
            }
            Button(onClick = { onNavigate("personal-note") }) {
                Text(text = "개인 노트로 가기")
            }
            Button(onClick = { onNavigate("audio-call") }) {
                Text(text = "음성 통화로 가기")
            }

        }
    }
}

@Composable
fun KakaoLoginView(viewModel: KakaoAuthViewModel) {

    val isLoggedIn = viewModel.isLoggedIn.collectAsState()

    val loginStatusInfoTitle = if (isLoggedIn.value) "로그인 상태" else "로그아웃 상태"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            viewModel.kakaoLogin()
        }) {
            Text(text = "로그인 하기")
        }
        Button(onClick = {
            viewModel.kakaoLogout()
        }) {
            Text(text = "로그아웃 하기")
        }
        Text(text = loginStatusInfoTitle, textAlign = TextAlign.Center, fontSize = 20.sp)
    }
}