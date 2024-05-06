package com.ssafy.stab.webrtc.audiocall

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R


@Composable
fun ParticipantList(participants: List<String>) {
//    val onlineUsers = listOf("참가자1", "참가자2")
//    val offlineUsers = listOf("참가자3", "참가자4", "참가자5", "참가자6")

    val profileImg = painterResource(id = R.drawable.profile)
    val muteImg = painterResource(id = R.drawable.soundoff)
    val micImg = painterResource(id = R.drawable.soundon)

    Column(
        modifier = Modifier
            .background(Color(0xFF7591C6))
            .width(300.dp)
            .height(400.dp)
            .padding(16.dp)
    ) {
        LazyColumn {
            item {
                SectionTitle(title = "통화중")
            }
            items(participants) { user ->
                UserRow(user, profileImg, muteImg, micImg)
            }
            item {
                SectionTitle(title = "오프라인")
            }
//            items(offlineUsers) { user ->
//                UserRow(user, profileImg, muteImg, micImg)
//            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, color = Color.White, modifier = Modifier.padding(8.dp))
    Divider(color = Color.White, thickness = 1.dp)
}

@Composable
fun UserRow(
    user: String,
    profileImg: Painter,
    muteImg: Painter,
    micImg: Painter
) {
    var volume by remember { mutableFloatStateOf(0.5f) }
    var isMuted by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = profileImg, contentDescription = "프로필 이미지")
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(text = user)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = volume,
                onValueChange = { newVolume ->
                    if (!isMuted) {
                        volume = newVolume
                    }
                },
                enabled = !isMuted
            )
        }
        IconButton(onClick = { isMuted = !isMuted }) {
            Icon(
                painter = if (isMuted) muteImg else micImg,
                contentDescription = if (isMuted) "음소거" else "음소거 해제"
            )
        }
    }
}