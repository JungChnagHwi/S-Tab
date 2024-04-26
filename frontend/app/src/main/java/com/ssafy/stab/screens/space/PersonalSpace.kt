package com.ssafy.stab.screens.space

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R

@Composable
fun PersonalSpace() {
    Column(
        modifier = Modifier
            .background(Color(0xFFE9ECF5))
            .fillMaxSize()
    ) {
        Header()
        TitleBar()
        Divider(
            color = Color.Gray, // 선의 색상 설정
            thickness = 1.dp, // 선의 두께 설정
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp) // 선 주변에 수직 패딩 추가
        )
        NoteListSpace()
    }
}

@Composable
fun Header() {
    val glassImg = painterResource(id = R.drawable.glass)
    val settingsImg = painterResource(id = R.drawable.settings)
    val profileImg = painterResource(id = R.drawable.profile)
    Spacer(modifier = Modifier.height(15.dp))
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color = Color(0xFFDCE3F1))
                .width(200.dp)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = glassImg, contentDescription = null)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "검색")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Image(modifier = Modifier
            .width(30.dp)
            .height(30.dp), painter = settingsImg, contentDescription = null)
        Spacer(modifier = Modifier.width(20.dp))
        Image(modifier = Modifier
            .width(30.dp)
            .height(30.dp), painter = profileImg, contentDescription = null)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
fun TitleBar() {
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
