package com.ssafy.stab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.modals.CreateNoteModal
import com.ssafy.stab.screens.auth.Login
import com.ssafy.stab.screens.auth.SignUp
import com.ssafy.stab.screens.note.PersonalNote
import com.ssafy.stab.screens.space.SpaceRouters
import com.ssafy.stab.ui.theme.STabTheme
import com.ssafy.stab.webrtc.audiocall.AudioCallScreen
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel
import com.kakao.sdk.common.util.Utility
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.screens.note.NoteViewModel
import com.ssafy.stab.screens.space.NoteListViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keyHash = Utility.getKeyHash(this)
        Log.d("Key Hash", "$keyHash")
        super.onCreate(savedInstanceState)
        PreferencesUtil.init(this)
        val loginDetails = PreferencesUtil.getLoginDetails()
        setContent {
            STabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Routers()
                }
            }
        }
    }
}

@Composable
fun Routers(){

    val navController = rememberNavController()

    fun navigateTo(destination: String) {
        navController.navigate(destination)
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { Login(navController = navController) }
        composable("sign-up") { SignUp(onNavigate = { navigateTo(it) }) }
        composable("space") { SpaceRouters(homeNavController = navController) }
        composable("personal-note") { PersonalNote(viewModel = NoteViewModel(), navController = navController)}
        composable("audio-call") {
            // ViewModel 초기화
            val audioCallViewModel = viewModel<AudioCallViewModel>()
            AudioCallScreen(viewModel = audioCallViewModel)
        }
        composable("create-note") { CreateNoteModal({}, NoteListViewModel()) }
        composable("create-folder") { CreateFolderModal({}, NoteListViewModel()) }
    }
}