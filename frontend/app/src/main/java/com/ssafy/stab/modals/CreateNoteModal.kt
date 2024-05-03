package com.ssafy.stab.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R


@Composable
fun CreateNoteModal() {
    val blankwwImg = painterResource(id = R.drawable.blankww)
    val blankwhImg = painterResource(id = R.drawable.blankwh)
    val blankywImg = painterResource(id = R.drawable.blankyw)
    val blankyhImg = painterResource(id = R.drawable.blankyh)
    val linewwImg = painterResource(id = R.drawable.lineww)
    val linewhImg = painterResource(id = R.drawable.linewh)
    val lineywImg = painterResource(id = R.drawable.lineyw)
    val lineyhImg = painterResource(id = R.drawable.lineyh)
    val checkedwwImg = painterResource(id = R.drawable.checkedww)
    val checkedwhImg = painterResource(id = R.drawable.checkedwh)
    val checkedywImg = painterResource(id = R.drawable.checkedyw)
    val checkedyhImg = painterResource(id = R.drawable.checkedyh)
    val selectedImg = painterResource(id = R.drawable.selected)
    val notselectedImg = painterResource(id = R.drawable.notselected)



    Row(modifier= Modifier.fillMaxSize()) {
       // 왼쪽
        Column(
            modifier = Modifier
                .background(Color(0xFF7591C6))
                .fillMaxWidth(0.3f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Text(text = "새 노트북", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFFFFF))
            Spacer(modifier = Modifier.height(20.dp))
            TextField(value = "", onValueChange = {})
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "미리보기", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFFFFF))
            Spacer(modifier = Modifier.height(20.dp))
            Image(painter = lineyhImg, contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(256.dp))
       }
        // 오른쪽
        Column(modifier= Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFE9ECF5))
            .fillMaxSize()) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "템플릿 설정", fontSize = 24.sp, modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold)
            Divider(
                color = Color.Gray, // 선의 색상 설정
                thickness = 1.dp, // 선의 두께 설정
                modifier = Modifier.padding(vertical = 10.dp) // 선 주변에 수직 패딩 추가
            )
            Text(text = "크기  :  A4", fontSize = 20.sp, modifier = Modifier.padding(20.dp, 12.dp), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "색상  :  ", fontSize = 20.sp, modifier = Modifier.padding(20.dp, 12.dp), fontWeight = FontWeight.Bold)
                Image(painter = selectedImg, contentDescription = null)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "하얀색")
                Spacer(modifier = Modifier.width(20.dp))
                Image(painter = notselectedImg, contentDescription = null)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text="노란색")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "방향  :  ", fontSize = 20.sp, modifier = Modifier.padding(20.dp, 12.dp), fontWeight = FontWeight.Bold)
                Image(painter = selectedImg, contentDescription = null)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "가로")
                Spacer(modifier = Modifier.width(20.dp))
                Image(painter = notselectedImg, contentDescription = null)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "세로")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "디자인", fontSize = 24.sp, modifier = Modifier.padding(20.dp, 12.dp), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = blankwhImg, contentDescription = null,
                        modifier= Modifier
                            .width(120.dp)
                            .height(192.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "무지 노트")
                }
                Spacer(modifier = Modifier.width(30.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = linewhImg, contentDescription = null,
                        modifier= Modifier
                            .width(120.dp)
                            .height(192.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "줄 노트")
                }
                Spacer(modifier = Modifier.width(30.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = checkedwhImg, contentDescription = null,
                        modifier= Modifier
                            .width(120.dp)
                            .height(192.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "격자 노트")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "취소")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "생성")
                }
            }
        }
    }
}
