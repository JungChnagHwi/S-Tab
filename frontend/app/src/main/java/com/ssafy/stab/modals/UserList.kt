package com.ssafy.stab.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.data.note.User

@Composable
fun UserListModal(userList: SnapshotStateList<User>) {
    val profileImg = painterResource(id = R.drawable.profile)

    Column(
        modifier = Modifier
            .background(Color(0xFF7591C6))
            .width(300.dp)
            .height(400.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        LazyColumn {
            item {
                SectionTitle(title = "노트 참여자")
            }
            items(userList) { user ->
                UserRow(user.nickname, profileImg)
            }
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
    ) {
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
                .padding(end = 8.dp)
        ) {
            Text(text = user)
//            Spacer(modifier = Modifier.height(8.dp))
//            Slider(
//                value = volume,
//                onValueChange = { newVolume ->
//                    if (!isMuted) {
//                        volume = newVolume
//                    }
//                },
//                enabled = !isMuted
//            )
//        }
//        IconButton(onClick = { isMuted = !isMuted }) {
//            Icon(
//                painter = if (isMuted) muteImg else micImg,
//                contentDescription = if (isMuted) "음소거" else "음소거 해제"
//            )
//        }
        }
    }
}