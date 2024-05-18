package com.ssafy.stab.modals

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import com.ssafy.stab.util.SocketManager
import com.ssafy.stab.util.note.getTemplate
import java.time.LocalDateTime


@Composable
fun CreateNoteModal(closeModal: () -> Unit, viewModel: NoteListViewModel) {
    val selectedImg = painterResource(id = R.drawable.selected)
    val notselectedImg = painterResource(id = R.drawable.notselected)
    val selectPlainImg = painterResource(id = R.drawable.plain_white_portrait)
    val selectLinedImg = painterResource(id = R.drawable.lined_white_portrait)
    val selectGridImg = painterResource(id = R.drawable.grid_white_portrait)
    val closeImg = painterResource(id = R.drawable.modal_close)
    val folderId by viewModel.folderId.collectAsState()
    val socketManager = SocketManager.getInstance()

    val templateType = remember {
        mutableStateOf(TemplateType.Plain)
    }

    val backgroundColor = remember {
        mutableStateOf(BackgroundColor.White)
    }

    val direction = remember {
        mutableStateOf(Direction.Portrait)
    }

    val finalTemplateImg = getTemplate(templateType.value, backgroundColor.value, direction.value)

    var noteTitle by remember { mutableStateOf("제목 없는 노트") }


    fun createNoteResponseToNote(response: CreateNoteResponse): Note {
        return Note(
            noteId = response.noteId,
            title = response.title,
            totalPageCnt = response.totalPageCnt,
            updatedAt = response.updatedAt,
            isLiked = response.isLiked
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)) // 전체 모달에 radius 추가
            .fillMaxSize()
            .background(Color(0xFFDCE3F1))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7591C6))
                    .padding(horizontal = 16.dp, vertical = 8.dp) // 수직 패딩만 추가
            ) {
                Text(
                    text = "새 노트북",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = closeImg,
                    contentDescription = "취소",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { closeModal() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFCCD7EB))
                    .padding(horizontal = 24.dp, vertical = 24.dp), // 좌우 패딩 추가
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFCCD7EB))
                        .fillMaxWidth(0.3f)
                        .height(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "미리보기",
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                        .background(Color(0xFFCCD7EB))
                        .fillMaxWidth(0.8f)
                        .height(240.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "제목  :",
                            fontFamily = FontFamily.Default
                            )
                        TextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                        )
                    }
                    Text(
                        text = "크기  :  A4",
                        fontFamily = FontFamily.Default,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp) // 좌우 간격 추가
                    ) {
                        Text(
                            text = "색상  :",
                            fontFamily = FontFamily.Default,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Image(
                            painter = if (backgroundColor.value == BackgroundColor.White) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { backgroundColor.value = BackgroundColor.White }
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = "하얀색",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier
                                .clickable { backgroundColor.value = BackgroundColor.White }
                                .padding(5.dp, 0.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = if (backgroundColor.value == BackgroundColor.Yellow) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { backgroundColor.value = BackgroundColor.Yellow }
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = "노란색",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier
                                .clickable { backgroundColor.value = BackgroundColor.Yellow }
                                .padding(5.dp, 0.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp) // 좌우 간격 추가
                    ) {
                        Text(
                            text = "방향  :",
                            fontFamily = FontFamily.Default,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Image(
                            painter = if (direction.value == Direction.Landscape) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { direction.value = Direction.Landscape }
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = "가로",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier
                                .clickable { direction.value = Direction.Landscape }
                                .padding(5.dp, 0.dp)
                        )
                        Spacer(modifier = Modifier.width(25.dp))
                        Image(
                            painter = if (direction.value == Direction.Portrait) selectedImg else notselectedImg,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { direction.value = Direction.Portrait }
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = "세로",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier
                                .clickable { direction.value = Direction.Portrait }
                                .padding(5.dp, 0.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFCCD7EB)) // 템플릿 영역 색상 변경
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp) // 좌우 간격 추가
                    .fillMaxHeight(0.75f) // 높이 조정
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "템플릿 디자인",
                    fontFamily = FontFamily.Default,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp) // 간격 없이 전체 가로를 덮도록 설정
                        .background(Color(0xFFCCD7EB)) // 템플릿 텍스트 배경 색상
                        .padding(40.dp, 0.dp)
                )
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
                                .clickable {
                                    templateType.value = TemplateType.Plain
                                }  // 템플릿 선택 업데이트
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "무지 노트",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier.clickable {
                                templateType.value = TemplateType.Plain
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = selectLinedImg,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .height(120.dp)
                                .width(80.dp)
                                .clickable {
                                    templateType.value = TemplateType.Lined
                                }  // 템플릿 선택 업데이트
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "줄 노트",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier.clickable {
                                templateType.value = TemplateType.Lined
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = selectGridImg,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .height(120.dp)
                                .width(80.dp)
                                .clickable {
                                    templateType.value = TemplateType.Grid
                                }  // 템플릿 선택 업데이트
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "격자 노트",
                            fontFamily = FontFamily.Default,
                            modifier = Modifier.clickable {
                                templateType.value = TemplateType.Grid
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp)) // 간격 추가
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // 좌우 간격 추가
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        createNote(
                            folderId,
                            noteTitle,
                            backgroundColor.value,
                            templateType.value,
                            if (direction.value == Direction.Portrait) 1 else 0
                        ) { response ->
                            val note = createNoteResponseToNote(response)
                            viewModel.addNote(note)
                            PreferencesUtil.getShareSpaceState()
                                ?.let { socketManager.updateSpace(it, "NoteCreated", response) }
                        }
                        closeModal()
                    },
                    modifier = Modifier.padding(8.dp), // 버튼 패딩 추가
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "생성",
                        color = Color.White,
                        fontFamily = FontFamily.Default,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}