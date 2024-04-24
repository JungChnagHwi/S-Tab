package com.ssafy.stab.screens.space

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.stab.screens.note.PersonalNote
import com.ssafy.stab.screens.note.ShareNote

@Composable
fun SpaceRouters() {

    val navController = rememberNavController()

    fun navigateTo(destination: String) {
        navController.navigate(destination)
    }

    NavHost(navController = navController, startDestination = "personal-space") {
        composable("personal-space") { PersonalSpace(onNavigate = { navigateTo(it) }) }
        composable("share-space") { ShareSpace(onNavigate = { navigateTo(it) }) }
        composable("book-mark") { BookMark(onNavigate = { navigateTo(it) }) }
        composable("deleted") { Deleted(onNavigate = { navigateTo(it) }) }
        composable("personal-note") { PersonalNote(onNavigate = { navigateTo(it) }) }
        composable("share-note") { ShareNote(onNavigate = { navigateTo(it) }) }
    }
}
