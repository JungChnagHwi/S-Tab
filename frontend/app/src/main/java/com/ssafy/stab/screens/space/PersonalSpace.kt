package com.ssafy.stab.screens.space

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import com.ssafy.stab.data.PreferencesUtil

@Composable
fun PersonalSpace() {
    val folderId = PreferencesUtil.getNowLocation().nowLocation

    Column(
        modifier = Modifier
            .background(Color(0xFFE9ECF5))
            .fillMaxSize()
    ) {
        MyTitleBar()
        Divider(
            color = Color.Gray, // 선의 색상 설정
            thickness = 1.dp, // 선의 두께 설정
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp) // 선 주변에 수직 패딩 추가
        )
        NoteListSpace(folderId.toString())
    }
}


@Composable
fun MyTitleBar() {
    val myspImg = painterResource(id = R.drawable.mysp)
    val leftImg = painterResource(id = R.drawable.left)
    Row() {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .width(30.dp)
                    .height(30.dp) ,painter = myspImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "내 스페이스")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "> ··· >")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "상위 폴더명")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .height(30.dp)
                    .width(30.dp), painter = leftImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(fontSize = 24.sp, text="현재 폴더명")
            }
        }
        
    }
}