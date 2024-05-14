package com.ssafy.stab.screens.space.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.stab.apis.space.share.ShareSpaceList
import com.ssafy.stab.apis.space.share.getShareSpaceList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        _shareSpaceList.value = _shareSpaceList.value + newSpace
    }

    fun removeShareSpace(spaceId: String) {
        _shareSpaceList.value = _shareSpaceList.value.filter { it.spaceId != spaceId }
    }
}