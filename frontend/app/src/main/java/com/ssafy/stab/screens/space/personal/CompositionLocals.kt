package com.ssafy.stab.screens.space.personal


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.compositionLocalOf
import com.ssafy.stab.data.PreferencesUtil

val LocalPrevFolderTitle = compositionLocalOf { mutableStateOf("") }
val LocalNowFolderTitle = compositionLocalOf { mutableStateOf("내 스페이스") }

val LocalPrevFolderId = compositionLocalOf { mutableStateOf("") }
val LocalNowFolderId = compositionLocalOf { mutableStateOf(PreferencesUtil.getLoginDetails().rootFolderId) }
