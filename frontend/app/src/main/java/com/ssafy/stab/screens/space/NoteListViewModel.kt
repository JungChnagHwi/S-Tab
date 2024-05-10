package com.ssafy.stab.screens.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.space.folder.FileEntity
import com.ssafy.stab.apis.space.folder.Folder
import com.ssafy.stab.apis.space.folder.Note
import com.ssafy.stab.apis.space.folder.getFileList
import com.ssafy.stab.data.PreferencesUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteListViewModel() : ViewModel() {
    private val _combinedList = MutableStateFlow<List<FileEntity>>(emptyList())
    val combinedList = _combinedList.asStateFlow()

    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            getFileList(PreferencesUtil.getNowLocation().nowLocation.toString(),
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
    }


    private fun updateCombinedList(newItems: List<FileEntity>) {
        val currentList = _combinedList.value.toMutableList()
        currentList.addAll(newItems)
        _combinedList.value = currentList.toList()
        _combinedList.value = currentList.sortedByDescending { it.updatedAt }
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