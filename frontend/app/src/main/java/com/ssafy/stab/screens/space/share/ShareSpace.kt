package com.ssafy.stab.screens.space.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.ShareSpace
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.components.MarkdownScreen
import com.ssafy.stab.apis.space.share.User
import com.ssafy.stab.apis.space.share.getShareSpace
import com.ssafy.stab.apis.space.share.leaveShareSpace
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListSpace
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel
import com.ssafy.stab.webrtc.audiocall.AudioSessionViewModel
import com.ssafy.stab.webrtc.audiocall.Connection
import com.ssafy.stab.webrtc.audiocall.ParticipantListModal
import com.ssafy.stab.webrtc.fragments.PermissionsDialog
import com.ssafy.stab.webrtc.utils.PermissionManager

@Composable
fun ShareSpace(
    spaceId: String,
    rootFolderId: String,
    audioCallViewModel: AudioCallViewModel,
    spaceViewModel: SpaceViewModel,
    socketManager: SocketManager,
    onNote: (String) -> Unit
) {

    // 드롭다운 이미지와 드롭업 이미지 리소스를 로드합니다.
    val dropdownImg = painterResource(id = R.drawable.dropdown)
    val dropupImg = painterResource(id = R.drawable.dropup)

    // 높이 상태를 관리하기 위한 상태 변수입니다.
    // 초기값은 최대 높이인 320.dp로 설정합니다.
    val boxHeightState = remember { mutableStateOf(320.dp) }

    // webRTC에 필요한 정보 설정(context, sessionId, participantName)
    val context = LocalContext.current
    val loginDetails = remember { PreferencesUtil.getLoginDetails() }
    val userName = remember { loginDetails.userName }
    audioCallViewModel.sessionId.value = spaceId
    if (userName != null) { audioCallViewModel.participantName.value = userName }
    // 현재 공유 스페이스의 통화방 참여 여부 판단
    val currentCallState = PreferencesUtil.callState.collectAsState()
    val isCurrentSpaceActive = currentCallState.value.callSpaceId == spaceId && currentCallState.value.isInCall

    var shareSpaceList = remember { mutableStateListOf<ShareSpaceList>() }
    // 현재 보고 있는 공유 스페이스 정보 가져오기
    val (shareSpaceDetails, setShareSpaceDetails) = remember { mutableStateOf<ShareSpace?>(null) }
    val (totalUsers, setTotalUsers) = remember { mutableStateOf(listOf<String>()) }
    var showParticipantListModal by remember { mutableStateOf(false) }

    val audioSessionViewModel: AudioSessionViewModel = viewModel()
    val participants by audioSessionViewModel.participants.collectAsState()

    LaunchedEffect(spaceId) {
        getShareSpace(spaceId) { shareSpaceData ->
            setShareSpaceDetails(shareSpaceData)
            setTotalUsers(shareSpaceData.users.map { it.nickname })
        }
        audioSessionViewModel.getSessionConnection(spaceId)
        socketManager.joinSpace(spaceId, userName ?: "Unknown") // 소켓 통신 연결 - 공유 스페이스가 바뀌면 space room 연결
    }

    DisposableEffect(spaceId) {
        // Composable이 해제될 때, 스페이스 룸을 연결 종료
        onDispose {
            socketManager.leaveSpace(spaceId)
        }
    }

    LaunchedEffect(showParticipantListModal) {
        Log.d("ShareSpace", "showParticipantListModal: $showParticipantListModal")
    }

    Box(
        modifier = Modifier
            .background(Color(0xFFE9ECF5))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SpTitleBar(
                context = context,
                audioCallViewModel = audioCallViewModel,
                isCurrentSpaceActive = isCurrentSpaceActive,
                spaceId = spaceId,
                users = shareSpaceDetails?.users ?: listOf(),
                participants = participants,
                spaceViewModel = spaceViewModel,
                socketManager = socketManager,
                onShowParticipants = { showParticipantListModal = true }
            )
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
                MarkdownScreen(spaceId)
                // 드롭다운/드롭업 버튼을 배치합니다.
                // Box의 contentAlignment를 사용하여 버튼의 위치를 오른쪽 하단에 배치합니다.
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = if (boxHeightState.value == 320.dp) dropupImg else dropdownImg,
                        contentDescription = if (boxHeightState.value == 320.dp) "드롭다운" else "드롭업",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(10.dp)
                            .clickable {
                                // 높이 상태를 토글합니다.
                                boxHeightState.value =
                                    if (boxHeightState.value == 320.dp) 80.dp else 320.dp
                            }
                    )
                }
            }
            NoteListSpace(rootFolderId, onNote)
        }

        if (showParticipantListModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { showParticipantListModal = false }
                    )
            ) {
                ParticipantListModal(
                    totalusers = shareSpaceDetails?.users ?: listOf(),
                    participants = participants,
                    sessionId = spaceId,
                    onDismiss = { showParticipantListModal = false }
                )
            }
        }
    }
}

@Composable
fun SpTitleBar(
    context: Context,
    audioCallViewModel: AudioCallViewModel,
    isCurrentSpaceActive: Boolean,
    spaceId: String,
    users: List<User>,
    participants: List<Connection>,
    spaceViewModel: SpaceViewModel,
    socketManager: SocketManager,
    onShowParticipants: () -> Unit
) {
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val leftImg = painterResource(id = R.drawable.left)
    // 통화 방 참여 상태에 따른 이미지 리소스 결정
    val callActive = remember { mutableStateOf(isCurrentSpaceActive) }
    val callButtonImage = if (callActive.value) {
        painterResource(id = R.drawable.calloff)  // 통화 종료 아이콘
    } else {
        painterResource(id = R.drawable.call)  // 통화 시작 아이콘
    }
    val envelopeImg = painterResource(id = R.drawable.envelope)
    val outImg = painterResource(id = R.drawable.out)
    val peopleImg = painterResource(id = R.drawable.people)

    val showPopup = remember { mutableStateOf(false) }


    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    if (showPopup.value) {
        AlertDialog(
            onDismissRequest = {
                showPopup.value = false
            },
            title = {
                Text(text = "공유 코드")
            },
            text = {
                Text(text = spaceId)
            },
            confirmButton = {
                Button(
                    onClick = {
                        copyToClipboard(context, spaceId)
                        showPopup.value = false // 팝업 닫기
                    }
                ) {
                    Text("복사")
                }
            },
            dismissButton = {
                Button(onClick = { showPopup.value = false }) {
                    Text("취소")
                }}
        )}
    // 음성 권한 요청 dialog
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        PermissionsDialog(
            onPermissionGranted = {
                audioCallViewModel.buttonPressed(context)
                !callActive.value
            },
            onPermissionDenied = {
                // Handle permission denial, possibly notify user
            },
            onDialogDismiss = {
                showDialog.value = false
            }
        )
    }

//    var showParticipantListModal by remember { mutableStateOf(false) }
//
//    if (showParticipantListModal) {
//        ParticipantListModal(users, participants) {
//            showParticipantListModal = false  // 모달 닫기
//        }
//    }


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
                    Row(
                        modifier = Modifier.clickable {
                            onShowParticipants()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = peopleImg,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "(${participants.size} / ${users.size} )", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = callButtonImage,
                        contentDescription = if (callActive.value) "통화 종료" else "통화 시작",
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                            .clickable {
                                if (PermissionManager.arePermissionsGranted(context)) {
                                    audioCallViewModel.buttonPressed(context)
                                    callActive.value = !callActive.value
                                } else {
                                    showDialog.value = true
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = envelopeImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                            .clickable {
                                showPopup.value = true
                            }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = outImg,
                        contentDescription = null,
                        modifier = Modifier
                            .height(30.dp)
                            .height(30.dp)
                            .clickable {
                                leaveShareSpace(spaceId) {
                                    spaceViewModel.removeShareSpace(spaceId)
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }

    }
}
