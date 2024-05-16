package com.ssafy.stab.screens.space.personal


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import com.ssafy.stab.data.PreferencesUtil


val LocalSelectedFileId = compositionLocalOf { mutableStateOf("") }
val LocalSelectedFileTitle = compositionLocalOf { mutableStateOf("") }
val LocalNowFolderTitle = compositionLocalOf { mutableStateOf("") }
val LocalNowFolderId = compositionLocalOf { mutableStateOf(PreferencesUtil.getLoginDetails().rootFolderId) }
val LocalNavigationStackId = compositionLocalOf { mutableStateListOf<String>() }
val LocalNavigationStackTitle = compositionLocalOf { mutableStateListOf<String>() }