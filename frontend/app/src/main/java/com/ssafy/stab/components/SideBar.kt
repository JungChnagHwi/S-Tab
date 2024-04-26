package com.ssafy.stab.components

import androidx.compose.foundation.Image
import com.ssafy.stab.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideBar(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    val starImg = painterResource(id = R.drawable.star)
    val trashImg = painterResource(id = R.drawable.trash)
    val myspImg = painterResource(id = R.drawable.mysp)
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val wifiImg = painterResource(id = R.drawable.connection)
    val micImg = painterResource(id = R.drawable.mic)
    val speakerImg = painterResource(id = R.drawable.speaker)
    val phoneImg = painterResource(id = R.drawable.phone)
    val plusImg = painterResource(id = R.drawable.plus)

    val shareSpaceList = listOf(
        "스터디1",
        "스터디2",
        "스터디3",
        "스터디4",
        "스터디5",
        "스터디6",
        "스터디1",
        "스터디2",
        "스터디3",
        "스터디4",
        "스터디5",
        "스터디6",
    )

    Column( modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFDCE3F1))
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF5584FD))
                .align(Alignment.CenterHorizontally)
        ){
            Text(
                text = "S-Tab",
                fontSize = 28.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(50.dp))
            Image(painter = starImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "즐겨찾기",
                modifier = Modifier.clickable { onNavigate("book-mark") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(50.dp))
            Image(painter = trashImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "휴지통",
                modifier = Modifier.clickable { onNavigate("deleted") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(50.dp))
            Image(painter = myspImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "내 스페이스",
                modifier = Modifier.clickable { onNavigate("personal-space") }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(50.dp))
            Image(painter = sharespImg, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "공유 스페이스")
        }
        Spacer(modifier = Modifier.height(7.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(70.dp))
                Image(painter = plusImg, contentDescription = null)
                Spacer(modifier = Modifier.width(7.dp))
                Text(text = "새로 만들기")
            }
            Spacer(modifier = Modifier.height(7.dp))
            ShareSpaceListScreen({ onNavigate("share-space") },
                spaceNames = shareSpaceList
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(0xFF7591C6))
                .align(Alignment.CenterHorizontally)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Image(
                    painter = wifiImg,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = "음성 연결됨",
                        color = Color(0xff4ADE80),
                        fontSize = 16.sp
                    )
                    Text(text = "스터디1")
                }
                Image(painter = micImg, contentDescription = null, modifier = Modifier.size(24.dp))
                Image(painter = speakerImg, contentDescription = null, modifier = Modifier.size(24.dp))
                Image(painter = phoneImg, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ShareSpaceListScreen(onNavigate: (String) -> Unit, spaceNames: List<String>){
    val sharespImg = painterResource(id = R.drawable.sharesp)

    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
        items(spaceNames) {
            spaceName ->
            Row {
                Spacer(modifier = Modifier.width(70.dp))
                Row(modifier = Modifier.clickable { onNavigate("share-space") }) {
                    Image(painter = sharespImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text=spaceName,
                        modifier = Modifier.padding(7.dp)
                    )
                }
            }
        }
    }
}