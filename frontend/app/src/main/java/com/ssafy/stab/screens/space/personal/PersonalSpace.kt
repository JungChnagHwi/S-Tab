package com.ssafy.stab.screens.space.personal

import NoteListViewModelFactory
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.screens.space.NoteListSpace
import com.ssafy.stab.screens.space.NoteListViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun PersonalSpace(navController: NavController, onNote: (String) -> Unit) {
    val folderId = PreferencesUtil.getLoginDetails().rootFolderId
    val nowFolderId = remember { mutableStateOf(folderId) }
    val navigationStackId = remember { mutableStateListOf<String>() }
    val navigationStackTitle = remember { mutableStateListOf<String>() }

    val viewModel: NoteListViewModel = viewModel(factory = NoteListViewModelFactory(folderId.toString()))
    CompositionLocalProvider(
        LocalNavigationStackId provides navigationStackId,
        LocalNavigationStackTitle provides navigationStackTitle,
        LocalNowFolderId provides mutableStateOf(nowFolderId.value),
        LocalNowFolderTitle provides mutableStateOf("")
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFE9ECF5))
                .fillMaxSize()
        ) {
            MyTitleBar(navController, viewModel)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp))
            nowFolderId.value?.let { NoteListSpace(it, onNote) }
        }
    }
}


@Composable
fun MyTitleBar(navController: NavController, viewModel: NoteListViewModel) {
    val myspImg = painterResource(id = R.drawable.mysp)
    val leftImg = painterResource(id = R.drawable.left)
    val nowFolderId = LocalNowFolderId.current
    val nowFolderTitle = LocalNowFolderTitle.current
    val navigationStackId = LocalNavigationStackId.current
    val navigationStackTitle = LocalNavigationStackTitle.current

    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .width(30.dp)
                    .height(30.dp) ,painter = myspImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "내 스페이스", modifier = Modifier.clickable { navController.navigate("personal-space") })
                Spacer(modifier = Modifier.width(5.dp))
                if (navigationStackTitle.size > 1) {
                    Text(text = "> ··· >")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text= navigationStackTitle[navigationStackTitle.size - 2])
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (navigationStackId.isNotEmpty()) {
                    Image(modifier = Modifier
                        .clickable {
                            val previousFolderId =
                                navigationStackId.removeAt(navigationStackId.size - 1)
                            val previousFolderTitle =
                                navigationStackTitle.removeAt(navigationStackTitle.size - 1)
                            nowFolderId.value = previousFolderId
                            nowFolderTitle.value = previousFolderTitle
                            if (navigationStackId.size != 0) {
                                viewModel.updateFolderId(navigationStackId[navigationStackId.size - 1])
                            } else {
                                navController.navigate("personal-space")
                            }
                        }
                        .height(30.dp)
                        .width(30.dp), painter = leftImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                if (navigationStackTitle.size != 0) {
                    Text(fontSize = 24.sp, text= navigationStackTitle[navigationStackTitle.size - 1])
                } else {
                    Text(text = "내 스페이스", fontSize = 24.sp)
                }
            }
        }
    }
}