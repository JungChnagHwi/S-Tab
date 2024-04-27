package com.ssafy.stab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.screens.auth.KakaoAuthViewModel
import com.ssafy.stab.screens.auth.Login
import com.ssafy.stab.screens.auth.SignUp
import com.ssafy.stab.screens.space.SpaceRouters
import com.ssafy.stab.ui.theme.STabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        composable("login") { Login(onNavigate = { navigateTo(it) }) }
        composable("sign-up") { SignUp(onNavigate = { navigateTo(it) }) }
        composable("space") { SpaceRouters() }
    }
}