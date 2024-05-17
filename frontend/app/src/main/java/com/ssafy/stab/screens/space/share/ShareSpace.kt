package com.ssafy.stab.screens.space.share

import NoteListViewModelFactory
import android.annotation.SuppressLint
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.ShareSpace
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.components.MarkdownScreen
import com.ssafy.stab.apis.space.share.User
import com.ssafy.stab.apis.space.share.getShareSpace
import com.ssafy.stab.apis.space.share.leaveShareSpace
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CheckLeaveModal
import com.ssafy.stab.screens.space.NoteListSpace
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.screens.space.personal.LocalNavigationStackId
import com.ssafy.stab.screens.space.personal.LocalNavigationStackTitle
import com.ssafy.stab.screens.space.personal.LocalNowFolderId
import com.ssafy.stab.screens.space.personal.LocalNowFolderTitle
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel
import com.ssafy.stab.webrtc.audiocall.AudioSessionViewModel
import com.ssafy.stab.webrtc.audiocall.Connection
import com.ssafy.stab.webrtc.audiocall.ParticipantListModal
import com.ssafy.stab.webrtc.fragments.PermissionsDialog
import com.ssafy.stab.webrtc.utils.PermissionManager

@SuppressLint("UnrememberedMutableState")
@Composable
fun ShareSpace(
    navController: NavController,
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
    val boxHeightState = remember { mutableStateOf(220.dp) }

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
    var showParticipantListModal by remember { mutableStateOf(false) }

    val audioSessionViewModel: AudioSessionViewModel = viewModel()
    val participants by audioSessionViewModel.participants.collectAsState()
    val viewModel: NoteListViewModel = viewModel(factory = NoteListViewModelFactory(rootFolderId))

    val spaceTitle = remember { mutableStateOf("") }
    LaunchedEffect(spaceId) {
        getShareSpace(spaceId) { shareSpaceData ->
            setShareSpaceDetails(shareSpaceData)
            spaceTitle.value = shareSpaceData.title
        }
        if (shareSpaceDetails != null) {
            Log.d("SpaceDetails", shareSpaceDetails.users.toString())
        }
        audioSessionViewModel.getSessionConnection(spaceId)
        audioSessionViewModel.startFetchingSessionData(spaceId)
        socketManager.joinSpace(spaceId, userName ?: "Unknown") // 소켓 통신 연결 - 공유 스페이스가 바뀌면 space room 연결
        PreferencesUtil.saveShareSpaceState(spaceId)
    }

    DisposableEffect(spaceId) {
        onDispose {
            audioSessionViewModel.stopFetchingSessionData()
        }
    }
    val nowFolderId = remember { mutableStateOf(rootFolderId) }
    val navigationStackId = remember { mutableStateListOf<String>() }
    val navigationStackTitle = remember { mutableStateListOf<String>() }

    CompositionLocalProvider(
        LocalNavigationStackId provides navigationStackId,
        LocalNavigationStackTitle provides navigationStackTitle,
        LocalNowFolderId provides mutableStateOf(nowFolderId.value),
        LocalNowFolderTitle provides  mutableStateOf("")
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFE9ECF5))
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SpTitleBar(
                    navController = navController,
                    context = context,
                    audioCallViewModel = audioCallViewModel,
                    isCurrentSpaceActive = isCurrentSpaceActive,
                    spaceId = spaceId,
                    rootFolderId = rootFolderId,
                    users = shareSpaceDetails?.users ?: listOf(),
                    title = spaceTitle.value,
                    participants = participants,
                    spaceViewModel = spaceViewModel,
                    viewModel = viewModel,
                    onShowParticipants = { showParticipantListModal = true }
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                )
                Box(
                    modifier = Modifier
                        // boxHeightState를 사용하여 Box의 높이를 동적으로 조절합니다.
                        .fillMaxWidth()
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
                            painter = if (boxHeightState.value == 220.dp) dropupImg else dropdownImg,
                            contentDescription = if (boxHeightState.value == 220.dp) "드롭다운" else "드롭업",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(10.dp)
                                .clickable {
                                    // 높이 상태를 토글합니다.
                                    boxHeightState.value =
                                        if (boxHeightState.value == 220.dp) 80.dp else 220.dp
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
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 112.dp, end = 20.dp) // 패딩을 통해 위치 조정
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
    }

}

@Composable
fun SpTitleBar(
    navController: NavController,
    context: Context,
    audioCallViewModel: AudioCallViewModel,
    isCurrentSpaceActive: Boolean,
    spaceId: String,
    rootFolderId: String,
    users: List<User>,
    title: String,
    participants: List<Connection>,
    spaceViewModel: SpaceViewModel,
    viewModel: NoteListViewModel,
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
    val showPermissionDialog = remember { mutableStateOf(false) }    // 음성 권한 요청 dialog
    val showCheckDialog  = remember { mutableStateOf(false) }   // 스페이스 떠나기 확인 dialog

    val nowFolderId = LocalNowFolderId.current
    val nowFolderTitle = LocalNowFolderTitle.current
    val navigationStackId = LocalNavigationStackId.current
    val navigationStackTitle = LocalNavigationStackTitle.current

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    fun createDeepLinkUrl(shareCode: String): String {
        return "s-tab://invite?code=$shareCode"
    }

    val deepLinkUrl = createDeepLinkUrl(spaceId)
    if (showPopup.value) {
        AlertDialog(
            onDismissRequest = {
                showPopup.value = false
            },
            title = {
                Text(text = "공유 코드")
            },
            text = {
                Text(text = deepLinkUrl)
            },
            confirmButton = {
                Button(
                    onClick = {
                        copyToClipboard(context, deepLinkUrl)
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


    if (showPermissionDialog.value) {
        PermissionsDialog(
            onPermissionGranted = {
                audioCallViewModel.buttonPressed(context)
                !callActive.value
            },
            onPermissionDenied = {
                // Handle permission denial, possibly notify user
            },
            onDialogDismiss = {
                showPermissionDialog.value = false
            }
        )
    }


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
                if (navigationStackTitle.size > 1) {
                    Text(text = "> ··· >")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text= navigationStackTitle[navigationStackTitle.size - 2])
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(modifier = Modifier
                        .clickable {
                            if (navigationStackId.isNotEmpty()) {
                                val previousFolderId =
                                    navigationStackId.removeAt(navigationStackId.size - 1)
                                val previousFolderTitle =
                                    navigationStackTitle.removeAt(navigationStackTitle.size - 1)
                                nowFolderId.value = previousFolderId
                                nowFolderTitle.value = previousFolderTitle
                                if (navigationStackId.size != 0) {
                                    viewModel.updateFolderId(navigationStackId[navigationStackId.size - 1])
                                } else {
                                    navController.navigate("share-space/${spaceId}/${rootFolderId}")
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                        .height(30.dp)
                        .width(30.dp), painter = leftImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    if (navigationStackTitle.size != 0) {
                        Text(fontSize = 24.sp, text= navigationStackTitle[navigationStackTitle.size - 1])
                    } else {
                        Text(text = title, fontSize = 24.sp)
                    }
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
                                    showPermissionDialog.value = true
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
                                showCheckDialog.value = true
                            }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
    }
    CheckLeaveModal(
        showDialog = showCheckDialog.value,
        onDismiss = { showCheckDialog.value = false },
        onConfirm = {
            showCheckDialog.value = false
            leaveShareSpace(spaceId) {
                spaceViewModel.removeShareSpace(spaceId)
            }
            navController.navigate("personal-space")
            if (isCurrentSpaceActive) {
                audioCallViewModel.leaveSession()
            }
        }
    )
}
