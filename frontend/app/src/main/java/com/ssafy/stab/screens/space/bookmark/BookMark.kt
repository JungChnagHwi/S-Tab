package com.ssafy.stab.screens.space.bookmark

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.BookmardFolder
import com.ssafy.stab.apis.space.bookmark.BookmardNote
import com.ssafy.stab.apis.space.bookmark.BookmardPage
import com.ssafy.stab.apis.space.bookmark.getBookMarkList

@Composable
fun BookMark(navController: NavController){

    val folders = remember { mutableStateOf<List<BookmardFolder>>(emptyList()) }
    val notes = remember { mutableStateOf<List<BookmardNote>>(emptyList()) }
    val pages = remember { mutableStateOf<List<BookmardPage>>(emptyList()) }

    LaunchedEffect(key1 = true) {
        getBookMarkList(
            { res ->
                if (res != null) {
                    folders.value = res
                }
            },
            { res ->
                if (res != null) {
                    notes.value = res
                }
            },
            { res ->
                if (res != null) {
                    pages.value = res
                }
            })
    }

    Column(
        modifier = Modifier
            .background(Color(0xFFE9ECF5))
            .fillMaxSize()
    ) {
        BookMarkTitleBar(navController)
        Divider(
            color = Color.Gray, // 선의 색상 설정
            thickness = 1.dp, // 선의 두께 설정
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp) // 선 주변에 수직 패딩 추가
        )
        BookMarkUnder(folders.value, notes.value, pages.value, navController)
    }
}

@Composable
fun BookMarkTitleBar(navController: NavController) {
    val bookMarkImg = painterResource(id = R.drawable.star)
    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .width(40.dp)
                    .height(40.dp) ,painter = bookMarkImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(fontSize = 32.sp, text="즐겨찾기", modifier = Modifier.clickable { navController.navigate("book-mark") })
            }
        }

    }
}

@Composable
fun BookMarkUnder(
    folders: List<BookmardFolder>,
    notes: List<BookmardNote>,
    pages: List<BookmardPage>,
    navController: NavController
){

    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(15.dp))
            BookMarkListGridScreen(folders, notes, pages, navController)
        }
    }
}