package com.ssafy.stab.components

import android.util.Log
import androidx.compose.foundation.Image
import com.ssafy.stab.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val logoImg = painterResource(id = R.drawable.logoimg)

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
                    Log.d("이미지루트", PreferencesUtil.getLoginDetails().profileImg.toString())
                }
                .fillMaxWidth(0.8f)
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF5584FD))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = logoImg, // 이미지 리소스 사용
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit, // 이미지 스케일 조정
                    modifier = Modifier
                        .size(48.dp) // 이미지 크기 설정
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp)) // 이미지와 텍스트 사이의 간격 설정
                Text(
                    text = "S - Tab",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp, // 텍스트 크기 조정
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
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
        if (callState.value.isInCall) {
            CallStateBox(
                currentCallSpaceName = currentCallSpaceName,
                isMuted = audioCallViewModel.isMuted.value,
                toggleMic = { audioCallViewModel.toggleMic() },
                leaveSession = { audioCallViewModel.leaveSession() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ShareSpaceListScreen(navController: NavController, shareSpaceList: List<ShareSpaceList>){
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val callingImg = painterResource(id = R.drawable.calling)
    val nowFolderId = LocalNowFolderId.current

    val callState by PreferencesUtil.callState.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
        items(shareSpaceList) { shareSpace ->
            Row {
                Spacer(modifier = Modifier.width(70.dp))
                Row(
                    modifier = Modifier.clickable {
                        navController.navigate("share-space/${shareSpace.spaceId}/${shareSpace.rootFolderId}")
                        nowFolderId.value = shareSpace.spaceId
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = sharespImg, contentDescription = "Share Space Icon")
                    Spacer(modifier = Modifier.width(5.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),  // 오른쪽에 여백 추가
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shareSpace.title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),  // 텍스트의 오른쪽에 여백 추가
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (callState.callSpaceId == shareSpace.spaceId) {
                            Image(
                                painter = callingImg,
                                contentDescription = "Calling Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallStateBox(
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


    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0xFF7591C6))
    ) {
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
            Column(
                modifier = Modifier.weight(1f)  // 텍스트와 컬럼의 비중을 조절하여 고정된 공간을 확보
            ) {
                Text(
                    text = "음성 연결됨",
                    color = Color(0xff4ADE80),
                    fontSize = 16.sp
                )
                Text(
                    text = currentCallSpaceName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 112.dp) // 텍스트 최대 너비 설정
                )
            }
            Spacer(modifier = Modifier.width(16.dp))  // 아이콘과 텍스트 사이의 간격을 고정
            Image(
                painter = soundImg,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        toggleMic()
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))  // 두 번째 아이콘과의 간격을 고정
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
    }
    Spacer(modifier = Modifier.height(30.dp))
}
