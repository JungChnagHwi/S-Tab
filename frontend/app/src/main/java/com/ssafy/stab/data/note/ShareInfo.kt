package com.ssafy.stab.data.note

import com.ssafy.stab.data.note.response.PageDetail

data class User(
    val nickname: String,
    val profileImg: String
)

enum class Action {
    Add,
    Undo,
    Create
}

data class SocketPathInfo(
    val type: Action,
    val pageOrderPathInfo: PageOrderPathInfo
)

data class SocketPageInfo(
    val type: Action,
    val page: Int,
    val newPageDetail: PageDetail
)