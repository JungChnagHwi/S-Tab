package com.ssafy.stab.screens.space.share

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.apis.space.share.getShareSpaceList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

class SpaceViewModel : ViewModel() {
    private val _shareSpaceList = MutableStateFlow<List<ShareSpaceList>>(emptyList())
    val shareSpaceList: StateFlow<List<ShareSpaceList>> = _shareSpaceList

    fun updateShareSpaceList() {
        viewModelScope.launch {
            getShareSpaceList { res ->
                _shareSpaceList.value = res
            }
        }
    }

    fun addShareSpace(newSpace: ShareSpaceList) {
        _shareSpaceList.value = listOf(newSpace) + _shareSpaceList.value
    }

    fun removeShareSpace(spaceId: String) {
        _shareSpaceList.value = _shareSpaceList.value.filter { it.spaceId != spaceId }
    }

    fun renameShareSpace(spaceId: String, newTitle: String) {
        val currentTime = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        _shareSpaceList.value = _shareSpaceList.value.map { space ->
            Log.d("SpaceData", space.toString())
            if (space.spaceId == spaceId) {
                space.copy(title = newTitle, updateAt = currentTime, createAt = space.createAt)
            } else {
                space
            }
        }
    }
}