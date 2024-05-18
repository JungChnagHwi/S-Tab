package com.ssafy.stab.screens.note

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.BuildConfig
import com.ssafy.stab.R
import com.ssafy.stab.components.ChatBotScreen
import com.ssafy.stab.components.note.CallStateBox
import com.ssafy.stab.components.note.ColorOptions
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.PageInterfaceBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.components.note.StrokeOptions
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.util.gpt.ChatBotViewModel
import com.ssafy.stab.util.note.NoteControlViewModel
import com.ssafy.stab.util.note.NoteControlViewModelFactory
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun NoteScreen(
    noteId: String,
    spaceId: String,
    initialPageId: String,
    socketManager: SocketManager,
    navController: NavController,
    audioCallViewModel: AudioCallViewModel,
    currentCallSpaceName: String
){
    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(noteId))
    val personalSpaceId = PreferencesUtil.getLoginDetails().personalSpaceId ?: ""
    val userName = PreferencesUtil.getLoginDetails().userName ?: ""
    val profileImg = PreferencesUtil.getLoginDetails().profileImg
        ?: (BuildConfig.BASE_S3 + "/image/2024/05/08/3454673260/profileImage.png")

    val noteControlViewModel : NoteControlViewModel = viewModel(factory = NoteControlViewModelFactory(Pair(noteId, socketManager)))
    val chatBotViewModel = remember { ChatBotViewModel.getInstance() }

    val currentPage = remember { mutableIntStateOf(0) }
    val onPageChange = { page: Int -> currentPage.intValue = page }

    var showUserList by remember { mutableStateOf(false) }
    var showChatBot by remember { mutableStateOf(false) }
    val chatBotImg = painterResource(id = R.drawable.chatbot)

    val callState = PreferencesUtil.callState.collectAsState()

    LaunchedEffect(spaceId) {
        if (spaceId != personalSpaceId) {
            socketManager.joinNote(noteId, userName, profileImg)
        }
    }

    DisposableEffect(spaceId) {
        onDispose {
            if (spaceId != personalSpaceId) {
                socketManager.leaveNote(noteId)
                noteViewModel.savePage(noteControlViewModel.pathList)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFFB9CDFF))
                    .height(52.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.left),
                        contentDescription = "back",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                noteViewModel.savePage(noteControlViewModel.pathList)
                                navController.popBackStack()
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val noteTitle by noteViewModel.noteTitle.collectAsState()
                    Text(
                        text = noteTitle,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W500
                    )
                }
                PageInterfaceBar(
                    currentPage = currentPage.intValue,
                    viewModel = noteViewModel,
                )
                if (callState.value.isInCall) {
                    CallStateBox(
                        currentCallSpaceName = currentCallSpaceName,
                        isMuted = audioCallViewModel.isMuted.value,
                        toggleMic = { audioCallViewModel.toggleMic() },
                        leaveSession = {audioCallViewModel.leaveSession() })
                }
            }

            Row(
                modifier = Modifier
                    .background(Background)
                    .height(52.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlsBar(noteControlViewModel)
                Divider(
                    color = Color(0xFFCCD7ED),
                    modifier = Modifier
                        .height(28.dp)
                        .width(2.dp)
                )
                if (noteControlViewModel.penType == PenType.Pen || noteControlViewModel.penType == PenType.Highlighter) {
                    ColorOptions(noteControlViewModel)
                    Divider(
                        color = Color(0xFFCCD7ED),
                        modifier = Modifier
                            .height(28.dp)
                            .width(2.dp)
                    )
                }
                StrokeOptions(noteControlViewModel)
                Spacer(modifier = Modifier.weight(1f))
                // 참여 유저 리스트 UI 업데이트 필요
//                socketManager.userList.forEach { user ->
//                    Text(text = "Nickname: ${user.nickname}")
//                }
                if (spaceId != personalSpaceId) {
                    Image(
                        painter = painterResource(R.drawable.people),
                        contentDescription = "users",
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { }
                    )
                }
                Image(
                    painter = chatBotImg,
                    contentDescription = "ChatBot",
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { showChatBot = !showChatBot }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                PageList(noteViewModel, noteControlViewModel, initialPageId, onPageChange)

                if (showChatBot) {
                    Box(
                        modifier = Modifier
                            .width(400.dp)
                            .height(1400.dp)
                            .align(Alignment.TopEnd) // 오른쪽 끝에 정렬
                            .zIndex(2f) // 챗봇 화면의 zIndex를 높게 설정
                    ) {
                        ChatBotScreen(
                            viewModel = chatBotViewModel,
                            onDismiss = { showChatBot = false }
                        )
                    }
                }
            }
        }
    }
}