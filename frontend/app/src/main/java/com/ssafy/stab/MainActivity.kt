package com.ssafy.stab

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.modals.CreateNoteModal
import com.ssafy.stab.screens.auth.Login
import com.ssafy.stab.screens.auth.SignUp
import com.ssafy.stab.screens.space.SpaceRouters
import com.ssafy.stab.ui.theme.STabTheme
import com.kakao.sdk.common.util.Utility
import com.ssafy.stab.components.MarkdownScreen
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.webrtc.audiocall.AudioCallViewModel

class MainActivity : ComponentActivity() {
    private var socketManager: SocketManager?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        var keyHash = Utility.getKeyHash(this)
        Log.d("Key Hash", "$keyHash")
        super.onCreate(savedInstanceState)
        PreferencesUtil.init(this)
        PreferencesUtil.saveCallState(false, null) // 앱 시작 시 callState 초기화

        socketManager = SocketManager.getInstance()
        socketManager?.connectToSocket(BuildConfig.SOCKET_URL)

        val loginDetails = PreferencesUtil.getLoginDetails()
        setContent {
            STabTheme {
                val audioCallViewModel = viewModel<AudioCallViewModel>()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Routers(audioCallViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager?.disconnect()
    }

}

@Composable
fun Routers(
    audioCallViewModel: AudioCallViewModel
    ) {

    val navController = rememberNavController()
    val socketManager = SocketManager.getInstance()

    fun navigateTo(destination: String) {
        navController.navigate(destination)
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(navController = navController) {
                socketManager.connectToSocket(BuildConfig.SOCKET_URL)
                navigateTo("space")
            }
        }
        composable("sign-up") { SignUp(onNavigate = { navigateTo(it) }) }
        composable("space") {
            SpaceRouters(
                onLogin = { navController.navigate("login") },
                audioCallViewModel,
                socketManager
            ) }
        composable("create-note") { CreateNoteModal({}, NoteListViewModel("f")) }
        composable("create-folder") { CreateFolderModal({}, NoteListViewModel("f")) }
        composable("markdown") { MarkdownScreen("") }
    }
}