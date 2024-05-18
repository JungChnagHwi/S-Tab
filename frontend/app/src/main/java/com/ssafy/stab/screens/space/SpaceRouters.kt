package com.ssafy.stab.screens.space

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.ssafy.stab.apis.auth.checkNickName
import com.ssafy.stab.apis.auth.patchInfo
import com.ssafy.stab.apis.auth.s3uri
import com.ssafy.stab.apis.auth.s3uriForPatch
import com.ssafy.stab.components.SideBar
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.PatchAuth
import com.ssafy.stab.screens.auth.ImagePreview
import com.ssafy.stab.screens.note.NoteScreen
import com.ssafy.stab.screens.space.bookmark.BookMark
import com.ssafy.stab.screens.space.personal.PersonalSpace
import com.ssafy.stab.screens.space.share.ShareSpace
import com.ssafy.stab.screens.space.share.SpaceViewModel
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.screens.space.deleted.Deleted
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun SpaceRouters(
    onLogin: () -> Unit,
    audioCallViewModel: AudioCallViewModel,
    socketManager: SocketManager,
    inviteCode: String
) {
    val navController = rememberNavController()
    val spaceViewModel: SpaceViewModel = viewModel()

    // NavController의 현재 라우트를 추적
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val personalSpaceId = PreferencesUtil.getLoginDetails().personalSpaceId ?: "spaceId"

    val shareSpaceList by spaceViewModel.shareSpaceList.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        // "personal-note"와 "share-note"가 아닐 때만 SideBar를 렌더링

        if (currentRoute != "note/{noteId}/{spaceId}/{pageId}") {
            SideBar(navController, audioCallViewModel, spaceViewModel, modifier = Modifier.weight(0.25f), inviteCode)
        }
        Column(modifier = Modifier
            .weight(0.75f)
            .background(color = Color(0xFFE9ECF5))
        ) {
            if (currentRoute != "note/{noteId}/{spaceId}/{pageId}") {
                Header(onLogin)
            }
            NavHost(navController = navController, startDestination = "personal-space") {
                composable("personal-space") {
                    PersonalSpace(navController) { navController.navigate("note/$it/$personalSpaceId/p") }
                }
                composable("share-space/{spaceId}/{rootFolderId}") { backStackEntry ->
                    val spaceId = backStackEntry.arguments?.getString("spaceId")
                    val rootFolderId = backStackEntry.arguments?.getString("rootFolderId")
                    if (spaceId != null && rootFolderId != null) {
                        ShareSpace(
                            navController,
                            spaceId,
                            rootFolderId,  // rootFolderId 전달
                            audioCallViewModel,
                            spaceViewModel,
                            socketManager,
                        ) { navController.navigate("note/$it/$spaceId/p") }
                    }
                }
                composable("book-mark") { BookMark(navController) }
                composable("deleted") { Deleted(navController) }
                composable("note/{noteId}/{spaceId}/{pageId}") {backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                    val spaceId = backStackEntry.arguments?.getString("spaceId") ?: ""
                    val pageId = backStackEntry.arguments?.getString("pageId") ?: ""

                    val currentCallSpaceName = shareSpaceList.find { it.spaceId == PreferencesUtil.callState.value.callSpaceId }?.title ?: "Unknown Space"
                    NoteScreen(noteId, spaceId, pageId, socketManager, navController, audioCallViewModel, currentCallSpaceName)
                }
                dialog("patch-auth") {
                    PatchAuth(onDismiss = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun Header(onLogin: () -> Unit) {
    val loginDetails by PreferencesUtil.loginDetails.collectAsState()

    loginDetails?.let { details ->
        val profileImg = rememberAsyncImagePainter(model = details.profileImg)
        val socketManager = SocketManager.getInstance()
        var showMenu by remember { mutableStateOf(false) }
        var showEditProfileDialog by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = details.userName.toString(),
                fontSize = 20.sp,
                color = Color(0xFF5584FD),
                fontWeight = FontWeight.Bold
            )
            Text(text = "님 반갑습니다!", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Box {
                Image(
                    painter = profileImg,
                    contentDescription = null,
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { showMenu = true }
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("회원 정보 수정", color = Color.Gray) },
                        onClick = {
                            showMenu = false
                            showEditProfileDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("로그아웃", color = Color.Red) },
                        onClick = {
                            PreferencesUtil.saveLoginDetails(
                                isLoggedIn = false,
                                accessToken = "",
                                userName = "",
                                profileImg = "",
                                rootFolderId = "",
                                personalSpaceId = ""
                            )
                            onLogin()
                            socketManager.disconnect()
                            showMenu = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                onDismiss = { showEditProfileDialog = false },
                onSave = { newNickname, newImageUri ->
                    // 로직 추가: 서버에 프로필 업데이트 요청
                    Log.d("뭐가 문제야 최최종", "$newNickname + $newImageUri")
                    patchInfo(newNickname, newImageUri) { res ->
                        Log.d("뭐가 문제야 최종", res.toString())
                        PreferencesUtil.saveLoginDetails(
                            isLoggedIn = true,
                            accessToken = PreferencesUtil.getLoginDetails().accessToken.toString(),
                            userName = res.nickname,
                            profileImg = res.profileImg,
                            rootFolderId = res.rootFolderId,
                            personalSpaceId = res.privateSpaceId
                        )
                    }
                }
            )
        }
    }
}



@Composable
fun EditProfileDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var nickname by remember { mutableStateOf("") }
    var beforeImageUri by remember { mutableStateOf<Uri?>(null) }
    var afterImageUri by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            beforeImageUri = result.data?.data
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))  // 배경색과 둥근 모서리
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "프로필 수정",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        pickImageLauncher.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                ) {
                    Text(
                        "프로필 사진 선택",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(50.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        isValid = false
                                    },
                    label = { Text("새 닉네임") },
                    modifier = Modifier
                        .weight(1f)
                        .width(200.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { checkNickName(nickname) { isValid = it } },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(120.dp)
                ) {
                    Text("중복 확인")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

//            Button(onClick = {
//                val intent = Intent(Intent.ACTION_PICK)
//                intent.type = "image/*"
//                pickImageLauncher.launch(intent)
//            }) {
//                Text("프로필 사진 선택")
//            }
//            ImagePreview(imageUri = beforeImageUri, modifier = Modifier.height(150.dp).fillMaxWidth())
//
//            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onDismiss() },
                    shape = RoundedCornerShape(8.dp),) {
                    Text("취소")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        if (nickname.isBlank()) {
                            nickname = PreferencesUtil.getLoginDetails().userName.toString()
                        }

                        val afterImageUri = if (beforeImageUri == null) {
                            PreferencesUtil.getLoginDetails().profileImg.toString()
                        } else {
                            suspendCoroutine<String> { continuation ->
                                s3uriForPatch(context, Uri.parse(beforeImageUri.toString()), nickname) { res ->
                                    continuation.resume(res)
                                }
                            }
                        }

                        Log.d("뭐가 문제임?", nickname)
                        Log.d("뭐가 문제임?", beforeImageUri.toString())
                        Log.d("뭐가 문제임?", afterImageUri)

                        onSave(nickname, afterImageUri)
                        onDismiss()
                    }
                },
                    shape = RoundedCornerShape(8.dp),
                    enabled = isValid) {

                    Text("저장")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


