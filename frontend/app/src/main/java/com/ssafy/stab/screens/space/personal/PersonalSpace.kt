package com.ssafy.stab.screens.space.personal

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListSpace

@SuppressLint("UnrememberedMutableState")
@Composable
fun PersonalSpace(navController: NavController) {
    val folderId = PreferencesUtil.getLoginDetails().rootFolderId

    CompositionLocalProvider(
        LocalPrevFolderTitle provides mutableStateOf(""),
        LocalNowFolderTitle provides mutableStateOf("내 스페이스")
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFE9ECF5))
                .fillMaxSize()
        ) {
            MyTitleBar(navController)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp))
            NoteListSpace(folderId.toString(), navController)
        }
    }
}


@Composable
fun MyTitleBar(navController: NavController) {
    val myspImg = painterResource(id = R.drawable.mysp)
    val leftImg = painterResource(id = R.drawable.left)
    val prevFolderTitle = LocalPrevFolderTitle.current
    val nowFolderTitle = LocalNowFolderTitle.current

    Row() {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .width(30.dp)
                    .height(30.dp) ,painter = myspImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "내 스페이스", modifier = Modifier.clickable { navController.navigate("personal-space") })
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "> ··· >")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = prevFolderTitle.value)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .clickable {

                    }
                    .height(30.dp)
                    .width(30.dp), painter = leftImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(fontSize = 24.sp, text= nowFolderTitle.value)
            }
        }
        
    }
}