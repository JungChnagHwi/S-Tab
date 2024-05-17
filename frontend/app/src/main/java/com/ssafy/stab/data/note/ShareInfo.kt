package com.ssafy.stab.data.note

data class User(
    val nickname: String,
    val color: String
)

enum class Action {
    Add,
    Undo,
}

data class SocketPathInfo(
    val type: Action,
    val pageOrderPathInfo: PageOrderPathInfo
)