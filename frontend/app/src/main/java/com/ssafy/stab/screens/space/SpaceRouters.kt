package com.ssafy.stab.screens.space

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.R
import com.ssafy.stab.components.SideBar
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.PatchAuth
import com.ssafy.stab.screens.note.NoteViewModel
import com.ssafy.stab.screens.note.PersonalNote
import com.ssafy.stab.screens.note.ShareNote
import com.ssafy.stab.screens.space.bookmark.BookMark
import com.ssafy.stab.screens.space.personal.PersonalSpace
import com.ssafy.stab.screens.space.share.ShareSpace
import com.ssafy.stab.screens.space.share.SpaceViewModel
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.screens.space.deleted.Deleted
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel

@Composable
fun SpaceRouters(
    onLogin: () -> Unit,
    audioCallViewModel: AudioCallViewModel,
    socketManager: SocketManager
) {
    val navController = rememberNavController()
    val spaceViewModel: SpaceViewModel = viewModel()

    // NavController의 현재 라우트를 추적
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route


    Row(modifier = Modifier.fillMaxSize()) {
        // "personal-note"와 "share-note"가 아닐 때만 SideBar를 렌더링

        if (currentRoute != "personal-note/{noteId}" && currentRoute != "share-note") {
            SideBar(navController, audioCallViewModel, spaceViewModel, modifier = Modifier.weight(0.25f))
        }
        Column(modifier = Modifier
            .weight(0.75f)
            .background(color = Color(0xFFE9ECF5))
        ) {
            if (currentRoute != "personal-note/{noteId}" && currentRoute != "share-note") {
                Header(onLogin)
            }
            NavHost(navController = navController, startDestination = "personal-space") {
                composable("personal-space") {
                    PersonalSpace(navController) { navController.navigate("personal-note/$it") }
                }
                composable("share-space/{spaceId}/{rootFolderId}") { backStackEntry ->
                    val spaceId = backStackEntry.arguments?.getString("spaceId")
                    val rootFolderId = backStackEntry.arguments?.getString("rootFolderId")
                    if (spaceId != null && rootFolderId != null) {
                        ShareSpace(
                            spaceId,
                            rootFolderId,  // rootFolderId 전달
                            audioCallViewModel,
                            spaceViewModel,
                            socketManager,
                        ) { navController.navigate("personal-note/$it") }
                    }
                }
                composable("book-mark") { BookMark(navController) }
                composable("deleted") { Deleted(navController) }
                composable("personal-note/{noteId}") {backStackEntry ->
                    backStackEntry.arguments?.getString("noteId")
                        ?.let { PersonalNote(NoteViewModel(it), navController) }
                }
                composable("share-note") { ShareNote(navController) }
                dialog("patch-auth") {
                    PatchAuth(onDismiss = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun Header(onLogin: () -> Unit) {
    val glassImg = painterResource(id = R.drawable.glass)
    val settingsImg = painterResource(id = R.drawable.settings)
    val profileImg = painterResource(id = R.drawable.profile)
    Spacer(modifier = Modifier.height(15.dp))
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color = Color(0xFFDCE3F1))
                .width(200.dp)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = glassImg, contentDescription = null)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "검색")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Image(modifier = Modifier
            .width(30.dp)
            .height(30.dp), painter = settingsImg, contentDescription = null)
        Spacer(modifier = Modifier.width(20.dp))
        Image(modifier = Modifier
            .width(30.dp)
            .height(30.dp)
            .clickable {
                PreferencesUtil.saveLoginDetails(
                    isLoggedIn = false,
                    accessToken = "",
                    userName = "",
                    profileImg = "",
                    rootFolderId = ""
                )
                onLogin()
            }, painter = profileImg, contentDescription = null)
        Spacer(modifier = Modifier.width(20.dp))
    }
}