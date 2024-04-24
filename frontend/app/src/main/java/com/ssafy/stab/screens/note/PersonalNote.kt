package com.ssafy.stab.screens.note

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PersonalNote(navController: NavController){
    Row {
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "뒤로가기")
        }
        Text(text = "개인 노트")
    }
}