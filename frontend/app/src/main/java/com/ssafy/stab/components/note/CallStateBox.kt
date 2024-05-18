package com.ssafy.stab.components.note

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R

@Composable
fun CallStateBox(
    currentCallSpaceName: String,
    isMuted: Boolean,
    toggleMic: () -> Unit,
    leaveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wifiImg = painterResource(id = R.drawable.connection)
    val soundOnImg = painterResource(id = R.drawable.soundon)
    val soundOffImg = painterResource(id = R.drawable.soundoff)
    val phoneImg = painterResource(id = R.drawable.phone)

    val soundImg = if (isMuted) soundOffImg else soundOnImg

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0xFF7591C6))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)  // 아이콘 간격을 설정
    ) {
        Image(
            painter = wifiImg,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = currentCallSpaceName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 100.dp) // 텍스트 최대 너비 설정
        )
        Image(
            painter = soundImg,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clickable { toggleMic() }
        )
        Image(
            painter = phoneImg,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clickable { leaveSession() }
        )
    }
}