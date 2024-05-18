package com.ssafy.stab.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.apis.space.share.createShareSpace

@Composable
fun CreateShareSpaceModal(closeModal: () -> Unit, onSpaceCreated: (ShareSpaceList) -> Unit) {
    var shareSpaceName by remember { mutableStateOf("제목 없는 공간") }
    val sharespImg = painterResource(id = R.drawable.sharesp)

    Column(
        modifier = Modifier.padding(10.dp).background(color = Color(0xFFDCE3F1)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = sharespImg, contentDescription = null, modifier = Modifier.width(100.dp).height(100.dp))
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = shareSpaceName,
            onValueChange = { shareSpaceName = it },
            label = { Text("공유 스페이스 이름", fontFamily = FontFamily.Default) },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.6f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            )
        )
        Row(Modifier.padding(10.dp)) {
            Button(
                onClick = { closeModal() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary // 생성 버튼의 글자색 사용
                )
            ) {
                Text(text = "취소", fontFamily = FontFamily.Default)
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                createShareSpace(shareSpaceName) { newSpace ->
                    onSpaceCreated(newSpace)
                    closeModal()
                }
            }) {
                Text(text = "생성", fontFamily = FontFamily.Default)
            }
        }
    }
}