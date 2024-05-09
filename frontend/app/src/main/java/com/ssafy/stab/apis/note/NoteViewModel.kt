package com.ssafy.stab.apis.note

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ssafy.stab.data.note.response.PageListResponse

class PageListViewModel : ViewModel() {
    var pageList = mutableStateOf<PageListResponse?>(null)
        private set

    // ViewModel 사용해서 데이터 받아올지는 지금 작성해둔 코드 작동하는지 상황 보고 결정
}