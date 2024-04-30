package com.ssafy.stab.screens.space

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.components.SideBar
import com.ssafy.stab.modals.PatchAuth
import com.ssafy.stab.screens.note.PersonalNote
import com.ssafy.stab.screens.note.ShareNote

@Composable
fun SpaceRouters() {
    val navController = rememberNavController()

    // NavController의 현재 라우트를 추적
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    fun navigateTo(destination: String) {
        navController.navigate(destination)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // "personal-note"와 "share-note"가 아닐 때만 SideBar를 렌더링
        if (currentRoute != "personal-note" && currentRoute != "share-note") {
            SideBar(onNavigate = { navigateTo(it) }, modifier = Modifier.weight(0.25f))
        }

        NavHost(navController = navController, startDestination = "personal-space", modifier = Modifier.weight(0.75f)) {
            composable("personal-space") { PersonalSpace() }
            composable("share-space") { ShareSpace() }
            composable("book-mark") { BookMark() }
            composable("deleted") { Deleted() }
            composable("personal-note") { PersonalNote(navController) }
            composable("share-note") { ShareNote(navController) }
            dialog("patch-auth") {
                PatchAuth(onDismiss = { navController.popBackStack() })
            }
        }
    }
}