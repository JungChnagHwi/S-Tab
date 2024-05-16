package com.ssafy.stab.util.gpt

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.gpt.sendQuestion
import kotlinx.coroutines.launch

data class Message(val text: String, val isUser: Boolean)

class ChatBotViewModel : ViewModel() {
    val messages: SnapshotStateList<Message> = mutableStateListOf()

    fun addUserMessage(text: String) {
        messages.add(Message(text, true))
    }

    fun addBotMessage(text: String) {
        messages.add(Message(text, false))
    }

    fun sendMessage(question: String) {
        addUserMessage(question)
        viewModelScope.launch {
            sendQuestion(question) { response ->
                addBotMessage(response.answer)
                Log.d("GPTResponse", response.question + response.answer)
            }
        }
    }

    companion object {
        @Volatile private var instance: ChatBotViewModel? = null

        fun getInstance(): ChatBotViewModel {
            return instance ?: synchronized(this) {
                instance ?: ChatBotViewModel().also { instance = it }
            }
        }
    }
}