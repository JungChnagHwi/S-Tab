package com.ssafy.stab.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.note.CreateNoteResponse
import com.ssafy.stab.apis.space.note.createNote
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.screens.space.NoteListViewModel
import com.ssafy.stab.util.note.getTemplate
import java.time.LocalDateTime


@Composable
fun CreateNoteModal(closeModal: () -> Unit, viewModel: NoteListViewModel) {
    val selectedImg = painterResource(id = R.drawable.selected)
    val notselectedImg = painterResource(id = R.drawable.notselected)
    val selectPlainImg = painterResource(id = R.drawable.plain_white_portrait)
    val selectLinedImg = painterResource(id = R.drawable.lined_white_portrait)
    val selectGridImg = painterResource(id = R.drawable.grid_white_portrait)

    val templateType = remember {
        mutableStateOf(TemplateType.Plain)
    }

    val backgroundColor = remember {
        mutableStateOf(BackgroundColor.White)
    }

    val direction = remember {
        mutableStateOf(Direction.Portrait)
    }

    // ㅋㅋ 그저 "value"
    val finalTemplateImg = getTemplate(templateType.value, backgroundColor.value, direction.value)

    var noteTitle by remember { mutableStateOf("제목 없는 노트") }


    fun createNoteResponseToNote(response: CreateNoteResponse): Note {
        return Note(
            noteId = response.noteId,
            title = response.title,
            totalPageCnt = response.totalPageCnt,
            updatedAt = response.updatedAt ?: LocalDateTime.now(),
            isLiked = response.isLiked
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "취소", fontSize = 20.sp, modifier = Modifier.clickable { closeModal() })
            Text(text = "새 노트북", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "생성", fontSize = 20.sp, modifier = Modifier.clickable { createNote(
                PreferencesUtil.getLoginDetails().rootFolderId.toString(),
                noteTitle,
                backgroundColor.value,
                templateType.value,
                if (direction.value == Direction.Portrait) 1 else 0
            ) { response ->
                val note = createNoteResponseToNote(response)
                viewModel.addNote(note)
            }
                closeModal()
            })
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD9D9D9))
                    .fillMaxWidth(0.3f)
                    .height(240.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "내지 디자인", fontSize = 20.sp)
                Image(
                    painter = painterResource(id = finalTemplateImg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .height(if (direction.value == Direction.Portrait) 160.dp else 120.dp)
                        .width(if (direction.value == Direction.Portrait) 120.dp else 160.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD9D9D9))
                    .fillMaxWidth(0.8f)
                    .height(240.dp)
            ) {
                Row(modifier=Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "제목  :")
                    TextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                    )
                }
                Text(text = "크기  :  A4", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "색상  :", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
                    Image(
                        painter = if (backgroundColor.value == BackgroundColor.White) selectedImg else notselectedImg,
                        contentDescription = null,
                        modifier = Modifier.clickable { backgroundColor.value = BackgroundColor.White }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "하얀색", modifier = Modifier
                        .clickable { backgroundColor.value = BackgroundColor.White }
                        .padding(10.dp, 0.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = if (backgroundColor.value == BackgroundColor.Yellow) selectedImg else notselectedImg,
                        contentDescription = null,
                        modifier = Modifier.clickable { backgroundColor.value = BackgroundColor.Yellow }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "노란색", modifier = Modifier
                        .clickable { backgroundColor.value = BackgroundColor.Yellow }
                        .padding(10.dp, 0.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "방향  :", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
                    Image(
                        painter = if (direction.value == Direction.Landscape) selectedImg else notselectedImg,
                        contentDescription = null,
                        modifier = Modifier.clickable { direction.value = Direction.Landscape }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "가로", modifier = Modifier
                        .clickable { direction.value = Direction.Landscape }
                        .padding(10.dp, 0.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = if (direction.value == Direction.Portrait) selectedImg else notselectedImg,
                        contentDescription = null,
                        modifier = Modifier.clickable { direction.value = Direction.Portrait }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "세로", modifier = Modifier
                        .clickable { direction.value = Direction.Portrait }
                        .padding(10.dp, 0.dp))
                }

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.8f)
                .align(Alignment.CenterHorizontally),
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "템플릿", fontSize = 20.sp, modifier = Modifier.padding(40.dp, 0.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = selectPlainImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { templateType.value = TemplateType.Plain }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "무지 노트", modifier = Modifier.clickable { templateType.value = TemplateType.Plain })
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = selectLinedImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { templateType.value = TemplateType.Lined }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "줄 노트", modifier = Modifier.clickable { templateType.value = TemplateType.Lined })
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = selectGridImg,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(120.dp)
                            .width(80.dp)
                            .clickable { templateType.value = TemplateType.Grid }  // 템플릿 선택 업데이트
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "격자 노트", modifier = Modifier.clickable { templateType.value = TemplateType.Grid })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
