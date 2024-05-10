package com.ssafy.stab.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _pageList = MutableStateFlow(
        mutableListOf(
            PageData(
                "",
                BackgroundColor.White,
                TemplateType.Plain,
                1,
                false,
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                null,
                null
            )
        )
    )
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

    fun addPage(beforePageId: String, newPage: NewPage) {
        val newPageData = PageData(
            newPage.pageId,
            newPage.color,
            newPage.template,
            newPage.direction,
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
        val index = newPageList.indexOfFirst { it.pageId == beforePageId } + 1
        newPageList.add(index, newPageData)
        _pageList.value = newPageList
    }
}