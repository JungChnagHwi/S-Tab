package com.ssafy.stab.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

    var text by remember { mutableStateOf("제목 없는 노트") }
    var selectedTemplate by remember { mutableStateOf("line") }
    var selectedColor by remember { mutableStateOf("w") }
    var selectedOrientation by remember { mutableStateOf("h") }

    // UI에서 선택된 템플릿, 색상, 방향에 따라 적절한 이미지 리소스 결정
    val imageResource = when (selectedTemplate) {
        "blank" -> when (selectedColor) {
            "w" -> if (selectedOrientation == "h") blankwhImg else blankwwImg
            "y" -> if (selectedOrientation == "h") blankyhImg else blankywImg
            else -> blankwhImg
        }
        "line" -> when (selectedColor) {
            "w" -> if (selectedOrientation == "h") linewhImg else linewwImg
            "y" -> if (selectedOrientation == "h") lineyhImg else lineywImg
            else -> linewhImg
        }
        "checked" -> when (selectedColor) {
            "w" -> if (selectedOrientation == "h") checkedwhImg else checkedwwImg
            "y" -> if (selectedOrientation == "h") checkedyhImg else checkedywImg
            else -> checkedwhImg
        }
        else -> linewhImg
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "취소", fontSize = 20.sp)
            Text(text = "새 노트북", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "생성", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(
                Modifier.clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD9D9D9))
                    .fillMaxWidth(0.3f)
                    .height(240.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "내지 디자인", fontSize = 20.sp)
                Image(
                    painter = imageResource,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp)
                        .height(if (selectedOrientation == "h") 160.dp else 120.dp)
                        .width(if (selectedOrientation == "h") 120.dp else 160.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                Modifier.clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD9D9D9))
                    .fillMaxWidth(0.8f)
                    .height(240.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "색상:", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
                    listOf("하얀색" to "w", "노란색" to "y").forEach { (label, color) ->
                        Image(
                            painter = if (selectedColor == color) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier.clickable { selectedColor = color }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = label, modifier = Modifier.clickable { selectedColor = color })
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "방향:", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
                    listOf("가로" to "w", "세로" to "h").forEach { (label, orientation) ->
                        Image(
                            painter = if (selectedOrientation == orientation) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier.clickable { selectedOrientation = orientation }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = label, modifier = Modifier.clickable { selectedOrientation = orientation })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.8f)
                .align(Alignment.CenterHorizontally),
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "템플릿", fontSize = 20.sp, modifier = Modifier.padding(40.dp, 0.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = blankwhImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { selectedTemplate = "blank" }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "무지 노트", modifier = Modifier.clickable { selectedTemplate = "blank" })
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = linewhImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { selectedTemplate = "line" }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "줄 노트", modifier = Modifier.clickable { selectedTemplate = "line" })
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = checkedwhImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { selectedTemplate = "checked" }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "격자 노트", modifier = Modifier.clickable { selectedTemplate = "checked" })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
