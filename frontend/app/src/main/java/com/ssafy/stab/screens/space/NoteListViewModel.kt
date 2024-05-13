package com.ssafy.stab.screens.space

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.space.folder.FileEntity
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.folder.getFileList
import com.ssafy.stab.apis.space.share.getFileListShareSpace
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
            if (folderId[0] == 'f') {
                getFileList(
                    folderId,
                    { folders ->
                        val updatedFolders = folders ?: emptyList<Folder>()
                        updateCombinedList(updatedFolders)
                    },
                    { notes ->
                        val updatedNotes = notes ?: emptyList<Note>()
                        updateCombinedList(updatedNotes)
                    }
                )
            } else if (folderId[0] == 's') {
                getFileListShareSpace(
                    folderId,
                    { folders ->
                        val updatedFolders = folders ?: emptyList<Folder>()
                        updateCombinedList(updatedFolders)
                    },
                    { notes ->
                        val updatedNotes = notes ?: emptyList<Note>()
                        updateCombinedList(updatedNotes)
                    }
                )
            }
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
}