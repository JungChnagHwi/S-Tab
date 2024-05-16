package com.ssafy.stab.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ssafy.stab.util.gpt.ChatBotViewModel
import com.ssafy.stab.util.gpt.Message

@Composable
fun ChatBotModal (viewModel: ChatBotViewModel, onDismiss: () -> Unit) {
    var input by remember { mutableStateOf(TextFieldValue()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)) // Semi-transparent background for modal effect
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(viewModel.messages.size) { index ->
                    val message = viewModel.messages[viewModel.messages.size - 1 - index]
                    MessageBubble(message)
                }
            }
            Row(modifier = Modifier.padding(8.dp)) {
                BasicTextField(
                    value = input,
                    onValueChange = { newValue -> input = newValue },
                    modifier = Modifier.weight(1f).background(Color.Gray).padding(8.dp)
                )
                Button(
                    onClick = {
                        viewModel.sendMessage(input.text)
                        input = TextFieldValue()
                    }
                ) {
                    Text("보내기")
                }
            }
            Button(onClick = onDismiss) {
                Text("닫기")
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val backgroundColor = if (message.isUser) Color.Blue else Color.Gray
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        Text(message.text, color = Color.White)
    }
}