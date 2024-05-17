package com.ssafy.stab.components

import android.util.Log
import androidx.compose.foundation.Image
import com.ssafy.stab.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
//import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CreateShareSpaceModal
import com.ssafy.stab.modals.ParticipateModal
import com.ssafy.stab.screens.space.personal.LocalNowFolderId
import com.ssafy.stab.screens.space.share.SpaceViewModel
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel

@Composable
fun SideBar(navController: NavController, audioCallViewModel: AudioCallViewModel, spaceViewModel: SpaceViewModel = viewModel(), modifier: Modifier = Modifier, inviteCode: String) {
    val starImg = painterResource(id = R.drawable.star)
    val trashImg = painterResource(id = R.drawable.trash)
    val myspImg = painterResource(id = R.drawable.mysp)
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val plusImg = painterResource(id = R.drawable.plus)
    val participateImg = painterResource(id = R.drawable.participate)

    val showCreateModal = remember { mutableStateOf(false) }
    val showParticipateModal = remember { mutableStateOf(false) }
    val shareSpaceList by spaceViewModel.shareSpaceList.collectAsState()
    // 현재 통화 중인 스페이스의 이름 찾기
    val currentCallSpaceName = shareSpaceList.find { it.spaceId == PreferencesUtil.callState.value.callSpaceId }?.title ?: "Unknown Space"

    LaunchedEffect(key1 = true) {
        spaceViewModel.updateShareSpaceList()
    }

    val callState = PreferencesUtil.callState.collectAsState()

    if (showCreateModal.value) {
        Dialog(onDismissRequest = { showCreateModal.value = false }) {
            CreateShareSpaceModal(
                closeModal = { showCreateModal.value = false },
                onSpaceCreated = { newSpace ->
                    spaceViewModel.addShareSpace(newSpace)
                }
            )
        }
    }

    if (showParticipateModal.value) {
        Dialog(onDismissRequest = { showParticipateModal.value = false }) {
            ParticipateModal(
                closeModal = { showParticipateModal.value = false },
                onParticipateSuccess = {
                    spaceViewModel.updateShareSpaceList()
                },
                inviteCode
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDCE3F1))
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .clickable {
                    Log.d("액세스 토큰", PreferencesUtil.getLoginDetails().accessToken.toString())
                    Log.d("루트 폴더", PreferencesUtil.getLoginDetails().rootFolderId.toString())
                }
                .fillMaxWidth(0.8f)
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF5584FD))
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "S-Tab",
                fontSize = 28.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate("book-mark") }) {
                Image(painter = starImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "즐겨찾기")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate("deleted") }) {
                Image(painter = trashImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "휴지통")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate("personal-space") }) {
                Image(painter = myspImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "내 스페이스")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(50.dp))
            Image(painter = sharespImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "공유 스페이스")
        }
        Spacer(modifier = Modifier.height(7.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(70.dp))
                Row(
                    modifier = Modifier.clickable {
                        showCreateModal.value = true
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = plusImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(text = "생성하기")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(modifier = Modifier.clickable { showParticipateModal.value = true },
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = participateImg, contentDescription = null)
                    Text(text = "참가하기")
                }
            }
            Spacer(modifier = Modifier.height(7.dp))
            ShareSpaceListScreen(navController, shareSpaceList)
        }
        // 딥링크로 전달받은 초대 코드가 있을 경우 모달을 열기
        LaunchedEffect(inviteCode) {
            if (inviteCode.isNotEmpty()) {
                showParticipateModal.value = true
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        CallStateBox(
            currentCallSpaceName = currentCallSpaceName,
            isInCall = callState.value.isInCall,
            isMuted = audioCallViewModel.isMuted.value,
            isSpeakerMuted = audioCallViewModel.isSpeakerMuted.value,
            toggleMic = { audioCallViewModel.toggleMic() },
            toggleSpeaker = { audioCallViewModel.toggleSpeaker() },
            leaveSession = { audioCallViewModel.leaveSession() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ShareSpaceListScreen(navController: NavController, shareSpaceList: List<ShareSpaceList>){
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val callingImg = painterResource(id = R.drawable.calling)
    val nowFolderId = LocalNowFolderId.current

    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
        items(shareSpaceList) { shareSpace ->
            Row {
                Spacer(modifier = Modifier.width(70.dp))
                Row(modifier = Modifier.clickable {
                    navController.navigate("share-space/${shareSpace.spaceId}/${shareSpace.rootFolderId}")
                    nowFolderId.value = shareSpace.spaceId
                }) {
                    Image(painter = sharespImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = shareSpace.title , modifier = Modifier.padding(7.dp))
                }
            }
        }
    }
}

@Composable
fun CallStateBox(
    currentCallSpaceName: String,
    isInCall: Boolean,
    isMuted: Boolean,
    isSpeakerMuted: Boolean,
    toggleMic: () -> Unit,
    toggleSpeaker: () -> Unit,
    leaveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wifiImg = painterResource(id = R.drawable.connection)
    val noWifiImg = painterResource(id = R.drawable.no_connection)
    val soundOnImg = painterResource(id = R.drawable.soundon)
    val soundOffImg = painterResource(id = R.drawable.soundoff)
    val speakerOnImg = painterResource(id = R.drawable.speaker)
    val speakerOffImg = painterResource(id = R.drawable.speaker_off)
    val phoneImg = painterResource(id = R.drawable.phone)

    val speakerImg = if (isSpeakerMuted) speakerOffImg else speakerOnImg
    val soundImg = if (isMuted) soundOffImg else soundOnImg


    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0xFF7591C6))
    ) {
        if (isInCall) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = wifiImg,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = "음성 연결됨",
                        color = Color(0xff4ADE80),
                        fontSize = 16.sp
                    )
                    Text(text = currentCallSpaceName)
                }
                Image(
                    painter = soundImg,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            toggleMic()
                        }
                )
                Image(
                    painter = speakerImg,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            toggleSpeaker()
                        }
                )
                Image(
                    painter = phoneImg,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            leaveSession()
                        }
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = noWifiImg,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "참여 중인 통화가 없습니다.",
                    color = Color(0xFFE9ECF5)
                )
            }

        }
    }
    Spacer(modifier = Modifier.height(30.dp))
}

