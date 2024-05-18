package com.ssafy.stab.webrtc.audiocall

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.User
import com.ssafy.stab.modals.UserProfileImage


@Composable
fun ParticipantListModal(totalusers: List<User>, participants: List<Connection>, sessionId: String, onDismiss: () -> Unit) {

//    val profileImg = painterResource(id = R.drawable.profile)
    var profileImgUrl = "https://sixb-s-tab.s3.ap-northeast-2.amazonaws.com/image/2024/05/18/3470552700/e6610701-3998-467b-8b48-91a66896165f_content%253A%252F%252Fmedia%252Fexternal%252Fimages%252Fmedia%252F1000013707.jpeg"

    val muteImg = painterResource(id = R.drawable.soundoff)
    val micImg = painterResource(id = R.drawable.soundon)

    // participants에 있는 nickname들을 추출
    val participantNames = participants.map { connection ->
        val clientDataJson = connection.clientData ?: "{}"
        val clientDataObj = JsonParser.parseString(clientDataJson).asJsonObject
        if (clientDataObj.has("clientData")) clientDataObj.get("clientData").asString else "Unknown"
    }

    // 통화중인 사용자와 오프라인 사용자를 필터링
    val onlineUsers = totalusers.filter { user -> participantNames.contains(user.nickname) }
    val offlineUsers = totalusers.filter { user -> !participantNames.contains(user.nickname) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) // 모달 외부 클릭시 닫기
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd) // 오른쪽 위에 배치
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF7591C6))
                .width(250.dp)
                .height(400.dp)
                .padding(16.dp)
        ) {
            LazyColumn {
                item {
                    SectionTitle(title = "통화중")
                }
                items(onlineUsers) { user ->
                    if (user.profileImg != "null") {
                        profileImgUrl = user.profileImg
                    }
                    UserRow(user.nickname, profileImgUrl, muteImg, micImg)
                }
                item {
                    SectionTitle(title = "오프라인")
                }
                items(offlineUsers) { user ->
                    if (user.profileImg != "null") {
                        profileImgUrl = user.profileImg
                    }
                    UserRow(user.nickname, profileImgUrl, muteImg, micImg)
                }
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
    profileImgUrl: String,
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
//        Image(painter = profileImg, contentDescription = "프로필 이미지")
        UserProfileImage(profileImgUrl)
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
