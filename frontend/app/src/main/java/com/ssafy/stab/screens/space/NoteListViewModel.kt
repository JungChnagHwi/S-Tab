package com.ssafy.stab.screens.space

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.space.folder.FileEntity
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.folder.getFileList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteListViewModel(initialFolderId: String) : ViewModel() {
    private val _folderId = MutableStateFlow(initialFolderId)
    val folderId: StateFlow<String> = _folderId.asStateFlow()
    // 각 item의 옵션 상태를 관리할 Map
    private val _showOptionsMap = mutableMapOf<String, MutableState<Boolean>>()

    private val _combinedList = MutableStateFlow<List<FileEntity>>(emptyList())
    val combinedList = _combinedList.asStateFlow()
    init {
        loadFiles(_folderId.value)
    }

    private fun loadFiles(folderId: String) {
        viewModelScope.launch {
            _combinedList.value = emptyList()
            if (folderId != "") {
                getFileList(
                    folderId,
                    { folders ->
                        val updatedFolders = folders ?: emptyList<Folder>()
                        viewModelScope.launch {
                            updateCombinedList(updatedFolders)
                        }
                    },
                    { notes ->
                        val updatedNotes = notes ?: emptyList<Note>()
                        viewModelScope.launch {
                            updateCombinedList(updatedNotes)
                        }
                    }
                )
            } else {
            }
        }
    }


    private fun updateCombinedList(newItems: List<FileEntity>) {
        val newList = _combinedList.value.toMutableList().apply {
            addAll(newItems)
        }.sortedByDescending { it.updatedAt }

        _combinedList.value = newList
    }

    fun updateFolderId(newFolderId: String) {
        if (_folderId.value != newFolderId) {
            _folderId.value = newFolderId
            Log.d("NoteListViewModel", "Folder ID updated: $newFolderId")
            loadFiles(newFolderId)
        }
    }

    fun addNote(note: Note) {
        val newList = _combinedList.value.toMutableList().also { it.add(note) }
        _combinedList.value = newList.sortedByDescending { it.updatedAt }
        println("Added note, new list size: ${_combinedList.value.size}") // 로그 추가
    }

    fun addFolder(folder: Folder) {
        val newList = _combinedList.value.toMutableList().also { it.add(folder) }
        _combinedList.value = newList.sortedByDescending { it.updatedAt }
        println("Added folder, new list size: ${_combinedList.value.size}") // 로그 추가
    }
    fun renameFolder(folderId: String, newTitle: String) {
        viewModelScope.launch {
            val updatedList = _combinedList.value.map { item ->
                if (item is Folder && item.folderId == folderId) {
                    item.copy(title = newTitle)
                } else {
                    item
                }
            }
            _combinedList.value = updatedList
        }
        println("Renamed folder, new list size: ${_combinedList.value.size}") // 로그 추가
    }

    fun renameNote(noteId: String, newTitle: String) {
        viewModelScope.launch {
            val updatedList = _combinedList.value.map { item ->
                if (item is Note && item.noteId == noteId) {
                    item.copy(title = newTitle)
                } else {
                    item
                }
            }
            _combinedList.value = updatedList
        }
        println("Renamed note, new list size: ${_combinedList.value.size}") // 로그 추가
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            _combinedList.value = _combinedList.value.filterNot { it is Folder && it.folderId == folderId }
        }
        println("Deleted folder, new list size: ${_combinedList.value.size}") // 로그 추가
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _combinedList.value = _combinedList.value.filterNot { it is Note && it.noteId == noteId }
        }
        println("Deleted note, new list size: ${_combinedList.value.size}") // 로그 추가
    }

    fun closeAllOptions() {
        _showOptionsMap.values.forEach { it.value = false }
    }

    fun getShowOptionsState(id: String): MutableState<Boolean> {
        return _showOptionsMap.getOrPut(id) { mutableStateOf(false) }
    }
}