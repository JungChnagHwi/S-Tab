package com.ssafy.stab.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.data.PreferencesUtil


@Composable
fun Login(navController: NavController){
    val loginDetails =  PreferencesUtil.getLoginDetails()

    if (loginDetails.isLoggedIn){
        navController.navigate("space")
    } else {
        val kakaoAuthViewModel : KakaoAuthViewModel = viewModel()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            KakaoLoginView(kakaoAuthViewModel, navController = navController)

            Row {
                Button(onClick = { navController.navigate("sign-up") }) {
                    Text(text = "회원가입 페이지로 가기")
                }
                Button(onClick = { navController.navigate("space") }) {
                    Text(text = "개인 스페이스로 가기")
                }
                Button(onClick = {
                    Log.d("a", PreferencesUtil.getLoginDetails().accessToken.toString())
                }) {
                    Text(text = "액세스 토큰")
                }
                Button(onClick = { navController.navigate("markdown") }) {
                    Text(text = "마크 다운")
                }
            }
        }
    }
}

@Composable
fun KakaoLoginView(viewModel: KakaoAuthViewModel, navController: NavController) {

    val isLoggedIn = viewModel.isLoggedIn.collectAsState()

    val loginStatusInfoTitle = if (isLoggedIn.value) "로그인 상태" else "로그아웃 상태"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            viewModel.kakaoLogin(navController = navController)
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