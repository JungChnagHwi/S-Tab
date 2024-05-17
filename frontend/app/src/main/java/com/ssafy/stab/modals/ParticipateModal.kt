package com.ssafy.stab.modals

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.apis.space.share.getShareSpaceList
import com.ssafy.stab.apis.space.share.participateShareSpace

@Composable
fun ParticipateModal(closeModal: () -> Unit, onParticipateSuccess: () -> Unit, initialInviteCode: String="") {
    var shareSpaceCode by remember { mutableStateOf(initialInviteCode) }
    val sharespImg = painterResource(id = R.drawable.sharesp)

    Column(
        modifier = Modifier.padding(10.dp).background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = sharespImg, contentDescription = null, modifier = Modifier.width(100.dp).height(100.dp))
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = shareSpaceCode,
            onValueChange = { shareSpaceCode = it },
            label = { Text("초대 코드 입력") },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.6f)
        )
        Row(Modifier.padding(10.dp)) {
            Button(
                onClick = { closeModal() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(text = "취소")
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                participateShareSpace(shareSpaceCode) {
                    onParticipateSuccess()
                    closeModal()
                }
            }) {
                Text(text = "참가")
            }
        }
    }

}
