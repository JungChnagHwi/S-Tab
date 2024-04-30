package com.ssafy.stab.screens.space

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip

@Composable
fun NoteListSpace() {
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
            ListGridScreen()
        }
    }
}

@Composable
fun ListGridScreen() {
    val originalList = (1..72).toList()
    val specialItemsIndices = setOf(0)


    val listWithSpecialItems = originalList.flatMapIndexed { index, item ->
        when {
            index in specialItemsIndices -> listOf("createNote")
            index % 7 == 0 -> listOf("folder", item)
            else -> listOf(item)
        }
    }

    val notebookImg = painterResource(id = R.drawable.notebook)
    val createnoteImg = painterResource(id = R.drawable.createnote)
    val folderImg = painterResource(id = R.drawable.folder)
    val modiImg = painterResource(id = R.drawable.modi)
    val staronImg = painterResource(id = R.drawable.eachstaron)
    val staroffImg = painterResource(id = R.drawable.eachstaroff)

    LazyColumn {
        items(listWithSpecialItems.chunked(5)) { rowItems ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                for (item in rowItems) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                    ) {
                        when (item) {
                            is Int -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(contentAlignment = Alignment.TopEnd) {
                                        Image(
                                            painter = notebookImg, contentDescription = "노트",
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(160.dp)
                                        )
                                        // 즐겨찾기 아이콘을 오른쪽 상단에 겹치도록 배치합니다.
                                        Image(
                                            painter = staroffImg, contentDescription = "즐겨찾기",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(10.dp)
                                                .align(Alignment.TopEnd) // 여기에 align을 추가합니다
                                        )
                                    }
                                    Row {
                                        Text(text = "노트명 ${item}")
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Image(painter = modiImg, contentDescription = null,
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(20.dp))
                                    }
                                    Text(text = "2024-04-26")
                                    }
                                }
                            "createNote" -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(painter = createnoteImg, contentDescription = "새 노트 만들기",
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(20)))
                                    Text(text = "새로 만들기")
                                    }
                                }
                            "folder" -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(contentAlignment = Alignment.TopEnd) {
                                        Image(
                                            painter = folderImg, contentDescription = "폴더",
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(160.dp)
                                        )
                                        // 즐겨찾기 아이콘을 오른쪽 상단에 겹치도록 배치합니다.
                                        Image(
                                            painter = staronImg, contentDescription = "즐겨찾기",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(10.dp)
                                                .align(Alignment.TopEnd) // 여기에 align을 추가합니다
                                        )
                                    }
                                    Row {
                                        Text(text = "폴더명")
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Image(painter = modiImg, contentDescription = null,
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(20.dp))
                                    }
                                    Text(text = "2024-04-26")
                                }
                            }
                        }
                    }
                }
                // 남은 공간 채우기
                if (rowItems.size < 5) {
                    repeat(5 - rowItems.size) {
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp))
                    }
                }
            }
        }
    }
}