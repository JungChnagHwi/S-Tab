package com.ssafy.stab.components

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.apis.space.share.createShareSpace
import com.ssafy.stab.apis.space.share.getShareSpaceList
import com.ssafy.stab.modals.CreateFolderModal
import com.ssafy.stab.modals.CreateShareSpaceModal

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

    val showCreateModal = remember { mutableStateOf(false) }
    var shareSpaceList = remember { mutableStateListOf<ShareSpaceList>() }

    LaunchedEffect(key1 = true) {
        getShareSpaceList { res ->
            shareSpaceList.clear()
            shareSpaceList.addAll(res)
        }
    }

    Log.d("a", shareSpaceList.size.toString())

    if (showCreateModal.value) {
        Dialog(onDismissRequest = { showCreateModal.value = false }) {
            CreateShareSpaceModal(
                closeModal = { showCreateModal.value = false },
                onSpaceCreated = { newSpace ->
                    shareSpaceList.add(newSpace)
                }
            )
        }
    }

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
        Row{
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigate("book-mark") }) {
                Image(painter = starImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "즐겨찾기")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row{
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigate("deleted") }) {
                Image(painter = trashImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "휴지통")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigate("personal-space") }) {
                Image(painter = myspImg, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "내 스페이스")
            }
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
                modifier = Modifier.clickable {
                    showCreateModal.value = true
                                              },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(70.dp))
                Image(painter = plusImg, contentDescription = null)
                Spacer(modifier = Modifier.width(7.dp))
                Text(text = "새로 만들기")
            }
            Spacer(modifier = Modifier.height(7.dp))
            ShareSpaceListScreen({ onNavigate("share-space") }, shareSpaceList)
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
fun ShareSpaceListScreen(onNavigate: (String) -> Unit, shareSpaceList: List<ShareSpaceList>){
    val sharespImg = painterResource(id = R.drawable.sharesp)
    val callingImg = painterResource(id = R.drawable.calling)

    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
        items(shareSpaceList) { shareSpace ->
            Row {
                Spacer(modifier = Modifier.width(70.dp))
                Row(modifier = Modifier.clickable { onNavigate("share-space") }) {
                    Image(painter = sharespImg, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = shareSpace.title , modifier = Modifier.padding(7.dp))
                }
            }
        }
    }
}