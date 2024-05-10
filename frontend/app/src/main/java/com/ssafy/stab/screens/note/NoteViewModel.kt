package com.ssafy.stab.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.note.fetchPageList
import com.ssafy.stab.data.note.response.PageData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val _pageList = MutableStateFlow<MutableList<PageData>>(mutableListOf())
    val pageList = _pageList.asStateFlow()

    init {
        loadPageList()
    }

    private fun loadPageList() {
        viewModelScope.launch {
            fetchPageList("n-daabbf65-bc83-4939-ab9f-9f66798cea66") {
                _pageList.value = it.data
            }
        }
    }

}