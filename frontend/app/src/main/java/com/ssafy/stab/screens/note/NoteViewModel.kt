package com.ssafy.stab.screens.note

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.note.createNewPage
import com.ssafy.stab.apis.note.fetchPageList
import com.ssafy.stab.apis.note.savePageData
import com.ssafy.stab.apis.space.bookmark.addBookMark
import com.ssafy.stab.apis.space.bookmark.deleteBookMark
import com.ssafy.stab.data.note.Action
import com.ssafy.stab.data.note.PageOrderPathInfo
import com.ssafy.stab.data.note.SocketPageInfo
import com.ssafy.stab.data.note.UserPagePathInfo
import com.ssafy.stab.data.note.request.PageData
import com.ssafy.stab.data.note.request.SavingPageData
import com.ssafy.stab.data.note.response.PageDetail
import com.ssafy.stab.util.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(noteId: String) : ViewModel() {
    private val _noteId = MutableStateFlow(noteId)

    val socketManager = SocketManager.getInstance()

    private val _noteTitle = MutableStateFlow("")
    val noteTitle = _noteTitle.asStateFlow()

    private val _pageList = MutableStateFlow<MutableList<PageDetail>>(mutableListOf())
    val pageList = _pageList.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()

    init {
        loadPageList()
        socketManager.setNoteViewModel(this)
    }

    private fun loadPageList() {
        viewModelScope.launch {
            fetchPageList(_noteId.value) {
                _noteTitle.value = it.title
                _pageList.value = it.data.toMutableList()
                updateBookmarkStatus(0)
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
                0,
            )
            val newPageList = _pageList.value.toMutableList()
            newPageList.add(currentPage + 1, newPageDetail)

            if (socketManager.isNoteJoined) {
                val socketPageInfo = SocketPageInfo(Action.Create, currentPage + 1, newPageDetail)
                socketManager.updateNoteData(_noteId.value, socketPageInfo)
            }
            _pageList.value = newPageList
        }
    }

    fun socketAddPage(socketPageInfo: SocketPageInfo) {
        val newPageList = _pageList.value.toMutableList()
        newPageList.add(socketPageInfo.page, socketPageInfo.newPageDetail)
        _pageList.value = newPageList
    }

    fun savePage(undoPathList: MutableList<PageOrderPathInfo>) {
        _pageList.value.forEach { pageDetail ->
            val pathList = pageDetail.paths ?: mutableStateListOf()
            val updatePathList = undoPathList.filter { it.pageId == pageDetail.pageId }

            if (updatePathList.isNotEmpty()) {
                pathList.addAll(updatePathList.map { it.pathInfo })

                val pageData = PageData(
                    pathList
                )

                savePageData(SavingPageData(
                    pageDetail.pageId,
                    pageData
                ))
            }
        }
    }

    fun updateBookmarkStatus(currentPage: Int) {
        if (_pageList.value.isNotEmpty()) {
            _isBookmarked.value = _pageList.value.getOrNull(currentPage)?.isBookmarked ?: false
        }
    }

    fun addLikePage(currentPage: Int) {
        _pageList.value[currentPage].isBookmarked = true
        _isBookmarked.value = true
        addBookMark(_pageList.value[currentPage].pageId)
    }

    fun deleteLikePage(currentPage: Int) {
        _pageList.value[currentPage].isBookmarked = false
        _isBookmarked.value = false
        deleteBookMark(_pageList.value[currentPage].pageId)
    }
}