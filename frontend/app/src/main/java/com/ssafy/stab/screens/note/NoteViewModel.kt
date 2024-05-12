package com.ssafy.stab.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.note.createNewPage
import com.ssafy.stab.apis.note.fetchPageList
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType
import com.ssafy.stab.data.note.response.NewPage
import com.ssafy.stab.data.note.response.PageData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NoteViewModel : ViewModel() {
    private val _pageList = MutableStateFlow<MutableList<PageData>>(mutableListOf())
    val pageList = _pageList.asStateFlow()

    init {
        loadPageList()
    }

    private fun loadPageList() {
        viewModelScope.launch {
            fetchPageList("n-0704c37a-b857-45e2-89f0-04cb24d11f15") {
                _pageList.value = it.data
            }
        }
    }

    fun addPage(currentPage: Int) {
        createNewPage(_pageList.value[currentPage].pageId) {
            val newPageData = PageData(
                it.pageId,
                it.color,
                it.template,
                it.direction,
                false,
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                null,
                null
            )
            val newPageList = _pageList.value.toMutableList()
            newPageList.add(currentPage + 1, newPageData)
            _pageList.value = newPageList
        }
    }
}