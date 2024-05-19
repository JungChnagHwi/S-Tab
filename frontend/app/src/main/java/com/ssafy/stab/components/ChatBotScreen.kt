package com.ssafy.stab.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.stab.R
import com.ssafy.stab.util.gpt.ChatBotViewModel
import com.ssafy.stab.util.gpt.Message

@Composable
fun ChatBotScreen (viewModel: ChatBotViewModel, onDismiss: () -> Unit) {
    var input by remember { mutableStateOf(TextFieldValue()) }
    val closeImg = painterResource(id = R.drawable.chat_close)
    val sendImg = painterResource(id = R.drawable.chat_send)


    Box(
        modifier = Modifier
            .width(400.dp)
            .height(1400.dp)
            .background(Color(0xFF7591C6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFF253A70)) // 상단 공간 배경색
            ) {
                Text(
                    text = "AI Assistant",
                    fontFamily = FontFamily.Default,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                Image(
                    painter = closeImg,
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .align(Alignment.CenterEnd)
                        .clickable { onDismiss() }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp), // 위아래 간격 추가
                reverseLayout = true
            ) {
                // 최신 메세지가 아래에 보이도록 설정
                items(viewModel.messages.size) { index ->
                    val message = viewModel.messages[viewModel.messages.size - 1 - index]
                    MessageBubble(message)
                }
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White)
                    .height(56.dp) // 텍스트 입력창의 높이 설정
                    .padding(horizontal = 16.dp)
            ) {
                BasicTextField(
                    value = input,
                    onValueChange = { newValue -> input = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 56.dp) // 이미지를 위한 오른쪽 패딩 설정
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                )
                Image(
                    painter = sendImg,
                    contentDescription = "chatGPT에게 질문 보내기",
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .clickable {
                            viewModel.sendMessage(input.text)
                            input = TextFieldValue()
                        }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val backgroundColor = if (message.isUser) Color(0xFF5584FD) else Color(0xFFE9ECF5)
    val textColor = if (message.isUser) Color.White else Color(0xFF253A70)
    val shape = if (message.isUser) RoundedCornerShape(
        topStart = 16.dp, topEnd = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp
    ) else RoundedCornerShape(
        topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp
    )
    val chatBotImg = painterResource(id = R.drawable.assistance_icon)

    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        if (!message.isUser) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 2.dp)
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = chatBotImg,
                    contentDescription = "Bot Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
                Text(
                    text = "AI Assistant",
                    fontFamily = FontFamily.Default,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                color = backgroundColor,
                shape = shape,
                modifier = Modifier
                    .wrapContentSize() // 텍스트 길이에 따라 가로 크기 조정
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    AnimatedContent(
                        targetState = message.text,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { targetText ->
                        Text(
                            targetText,
                            color = textColor,
                            fontFamily = FontFamily.Default,
                            textAlign = TextAlign.Start // 왼쪽 정렬
                        )
                    }
                }
            }
        }
    }
}