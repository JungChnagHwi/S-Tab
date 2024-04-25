package com.ssafy.stab.components

import androidx.compose.foundation.Image
import com.ssafy.stab.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideBar(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    val starImg = painterResource(id = R.drawable.star)
    val trashImg = painterResource(id = R.drawable.trash)
    val myspImg = painterResource(id = R.drawable.mysp)
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val propertyImg = painterResource(id = R.drawable.property)
    
    Column( modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFE9ECF5))
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF5584FD))
                .align(Alignment.CenterHorizontally)
        ){
            Text(
                text = "S-Tab",
                fontSize = 28.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(40.dp))
            Image(painter = starImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "즐겨찾기",
                modifier = Modifier.clickable { onNavigate("book-mark") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(40.dp))
            Image(painter = trashImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "휴지통",
                modifier = Modifier.clickable { onNavigate("deleted") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(40.dp))
            Image(painter = myspImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "내 스페이스",
                modifier = Modifier.clickable { onNavigate("personal-space") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(40.dp))
            Image(painter = sharespImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "공유 스페이스",
                modifier = Modifier.clickable { onNavigate("share-space") }
            )
        }
//        Text(
//            text = "개인 노트",
//            modifier = Modifier.clickable { onNavigate("personal-note") }
//        )
//        Text(
//            text = "공유 노트",
//            modifier = Modifier.clickable { onNavigate("share-note") }
//        )
//        Text(
//            text = "회원 정보 수정 / 로그아웃",
//            modifier = Modifier.clickable { onNavigate("patch-auth") }
//        )
    }
}