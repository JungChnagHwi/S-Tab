package com.ssafy.stab.screens.space

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R

@Composable
fun ShareSpace() {
    // 드롭다운 이미지와 드롭업 이미지 리소스를 로드합니다.
    val dropdownImg = painterResource(id = R.drawable.dropdown)
    val dropupImg = painterResource(id = R.drawable.dropup)

    // 높이 상태를 관리하기 위한 상태 변수입니다.
    // 초기값은 최대 높이인 300.dp로 설정합니다.
    val boxHeightState = remember { mutableStateOf(300.dp) }

    Column(
        modifier = Modifier
            .background(Color(0xFFE9ECF5))
            .fillMaxSize()
    ) {
        SpTitleBar()
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // boxHeightState를 사용하여 Box의 높이를 동적으로 조절합니다.
                .height(boxHeightState.value)
                .padding(20.dp)
                .background(color = Color.White)
        ) {
            // TextField 등 입력란을 여기에 배치할 수 있습니다.

            // 드롭다운/드롭업 버튼을 배치합니다.
            // Box의 contentAlignment를 사용하여 버튼의 위치를 오른쪽 하단에 배치합니다.
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = if (boxHeightState.value == 300.dp) dropupImg else dropdownImg,
                    contentDescription = if (boxHeightState.value == 300.dp) "드롭다운" else "드롭업",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(10.dp)
                        .clickable {
                            // 높이 상태를 토글합니다.
                            boxHeightState.value =
                                if (boxHeightState.value == 300.dp) 80.dp else 300.dp
                        }
                )
            }
        }
        NoteListSpace()
    }
}

@Composable
fun SpTitleBar() {
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val leftImg = painterResource(id = R.drawable.left)
    val callImg = painterResource(id = R.drawable.call)
    val envelopeImg = painterResource(id = R.drawable.envelope)
    val outImg = painterResource(id = R.drawable.out)
    val peopleImg = painterResource(id = R.drawable.people)

    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .width(30.dp)
                    .height(30.dp) ,painter = sharespImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "공유 스페이스")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "> ··· >")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "상위 폴더명")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(modifier = Modifier
                        .height(30.dp)
                        .width(30.dp), painter = leftImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(fontSize = 24.sp, text="현재 폴더명")
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = peopleImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "( 2 / 6 )", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = callImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = envelopeImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = outImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }

    }
}
