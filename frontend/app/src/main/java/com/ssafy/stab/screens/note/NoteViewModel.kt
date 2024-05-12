package com.ssafy.stab.screens.note

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.note.createNewPage
import com.ssafy.stab.apis.note.fetchPageList
import com.ssafy.stab.apis.note.savePageData
import com.ssafy.stab.data.note.UserPagePathInfo
import com.ssafy.stab.data.note.request.PageData
import com.ssafy.stab.data.note.request.PageInfo
import com.ssafy.stab.data.note.request.SavingPageData
import com.ssafy.stab.data.note.request.convertPageInfoToPageData
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.util.note.NoteControlViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NoteViewModel : ViewModel() {
    private val _pageList = MutableStateFlow<MutableList<PageDetail>>(mutableListOf())
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
            val newPageDetail = PageDetail(
                it.pageId,
                it.color,
                it.template,
                it.direction,
                false,
                null,
                null,
            )
            val newPageList = _pageList.value.toMutableList()
            newPageList.add(currentPage + 1, newPageDetail)
            _pageList.value = newPageList
        }
    }

    fun savePage(undoPathList: MutableList<UserPagePathInfo>) {
        _pageList.value.forEach { pageDetail ->
            val pathList = pageDetail.paths ?: mutableStateListOf()
            val updatePathList = undoPathList.filter { it.pageId == pageDetail.pageId }

            if (updatePathList.isNotEmpty()) {
                pathList.addAll(updatePathList.map { it.pathInfo })

                val pageData = convertPageInfoToPageData(
                    PageInfo(
                        pathList,
                        pageDetail.figures ?: mutableStateListOf(),
                        pageDetail.textBoxes ?: mutableStateListOf(),
                        pageDetail.images ?: mutableStateListOf()
                    )
                )

                savePageData(SavingPageData(
                    pageDetail.pageId,
                    pageData.toString()
                ))

            }
        }
    }
}