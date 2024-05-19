package com.ssafy.stab.screens.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.data.PreferencesUtil


@Composable
fun Login(navController: NavController, onLoginSuccess: () -> Unit){
    val loginDetails =  PreferencesUtil.getLoginDetails()

    if (loginDetails.isLoggedIn){
        onLoginSuccess()  // 로그인된 상태일 때 소켓 초기화 및 연결
        navController.navigate("space")
    } else {
        val kakaoAuthViewModel : KakaoAuthViewModel = viewModel()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.landing_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Image(
                    painter = painterResource(id = R.drawable.landing_item),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 2000.dp, height = 800.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(200.dp))
            }
//            Row {
//                Button(onClick = { navController.navigate("sign-up") }) {
//                    Text(text = "회원가입 페이지로 가기")
//                }
//
//            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 150.dp, bottom = 100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_login),
                    contentDescription = "Kakao Login Button",
                    modifier = Modifier
                        .size(width = 400.dp, height = 150.dp)
                        .clickable {
                            kakaoAuthViewModel.kakaoLogin(navController = navController)
                        }
                )
            }
        }
    }
}

