package com.ssafy.stab.util.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.stab.util.SocketManager

class NoteControlViewModelFactory(private val noteId: String, private val socketManager: SocketManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteControlViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteControlViewModel(noteId, socketManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}