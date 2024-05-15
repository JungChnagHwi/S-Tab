package com.ssafy.stab.screens.space.bookmark

import android.util.Log
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
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.bookmark.BookmardFolder
import com.ssafy.stab.apis.space.bookmark.BookmardNote
import com.ssafy.stab.apis.space.bookmark.BookmardPage
import com.ssafy.stab.apis.space.bookmark.getBookMarkList

@Composable
fun BookMark(){
    val folders = remember { mutableStateOf<List<BookmardFolder>>(emptyList()) }
    val notes = remember { mutableStateOf<List<BookmardNote>>(emptyList()) }
    val pages = remember { mutableStateOf<List<BookmardPage>>(emptyList()) }

    LaunchedEffect(key1 = true) {
        getBookMarkList(
            { res ->
                Log.d("즐찾1", res.toString())
                if (res != null) {
                    folders.value = res
                }
            },
            { res ->
                Log.d("즐찾2", res.toString())
                if (res != null) {
                    notes.value = res
                }
            },
            { res ->
                Log.d("즐찾3", res.toString())
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
        BookMarkTitleBar()
        Divider(
            color = Color.Gray, // 선의 색상 설정
            thickness = 1.dp, // 선의 두께 설정
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp) // 선 주변에 수직 패딩 추가
        )
        BookMarkUnder(folders.value, notes.value, pages.value)
    }
}

@Composable
fun BookMarkTitleBar() {
    val bookMarkImg = painterResource(id = R.drawable.star)
    val leftImg = painterResource(id = R.drawable.left)
    Row() {
        Spacer(modifier = Modifier.width(30.dp))
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(modifier = Modifier
                    .height(40.dp)
                    .width(40.dp), painter = leftImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Image(modifier = Modifier
                    .width(40.dp)
                    .height(40.dp) ,painter = bookMarkImg, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(fontSize = 32.sp, text="즐겨찾기")
            }
        }

    }
}

@Composable
fun BookMarkUnder(
    folders: List<BookmardFolder>,
    notes: List<BookmardNote>,
    pages: List<BookmardPage>
){
    val isPageSort = remember { mutableStateOf(false) }
    val isNameSort = remember { mutableStateOf(false) }
    val listImg = painterResource(id = R.drawable.list)

    Column {
        Spacer(modifier = Modifier.height(5.dp))
        // 날짜 / 이름 / 아이콘 보기 / 자세히 보기
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                    .clickable { isNameSort.value = !isNameSort.value }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (!isNameSort.value) Color(0xFF7A99D5) else Color(0xFFC3CCDE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "날짜",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isNameSort.value) Color(0xFF7A99D5) else Color(0xFFC3CCDE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "이름",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            Image(painter = listImg, contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp))
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(15.dp))
            BookMarkListGridScreen(folders, notes, pages)
        }
    }
}