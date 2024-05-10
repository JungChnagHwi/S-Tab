package com.ssafy.stab.screens.space

import android.util.Log
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

    private val _combinedList = MutableStateFlow<List<FileEntity>>(emptyList())
    val combinedList = _combinedList.asStateFlow()
    init {
        loadFiles(_folderId.value)
    }

    private fun loadFiles(folderId: String) {
        viewModelScope.launch {
            Log.d("B", folderId)
            _combinedList.value = emptyList()
            getFileList(folderId,
                { folders ->
                    val updatedFolders = folders ?: emptyList<Folder>()
                    updateCombinedList(updatedFolders)
                },
                { notes ->
                    val updatedNotes = notes ?: emptyList<Note>()
                    updateCombinedList(updatedNotes)
                }
            )
            Log.d("C", _combinedList.value.size.toString())
        }
    }


    private fun updateCombinedList(newItems: List<FileEntity>) {
        val currentList = _combinedList.value.toMutableList()
        currentList.addAll(newItems)
        _combinedList.value = currentList.sortedByDescending { it.updatedAt }
    }

    fun updateFolderId(newFolderId: String) {
        if (_folderId.value != newFolderId) {
            _folderId.value = newFolderId
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

}