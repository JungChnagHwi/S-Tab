package com.ssafy.stab.components.note

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.apis.note.createNewPage
import com.ssafy.stab.data.note.response.PageData
import com.ssafy.stab.screens.note.NoteViewModel
import com.ssafy.stab.util.note.NoteController

@Composable
fun PageInterfaceBar(
    viewModel: NoteViewModel,
    currentPageId: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OptionIcons(
            R.drawable.createpage,
            "create page"
        ) {
            createNewPage(currentPageId) {
                viewModel.addPage(currentPageId, it)
            }
        }
    }
}

@Composable
fun RowScope.OptionIcons(
    @DrawableRes resId: Int,
    desc: String,
    onClick: () -> Unit
) {
    val modifier = Modifier.size(40.dp)
    IconButton(onClick = onClick, modifier = modifier) {
        Image(
            painterResource(id = resId),
            contentDescription = desc,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}