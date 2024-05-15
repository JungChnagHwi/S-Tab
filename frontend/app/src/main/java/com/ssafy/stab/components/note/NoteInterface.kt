package com.ssafy.stab.components.note

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.screens.note.NoteViewModel

@Composable
fun PageInterfaceBar(
    currentPage: Int,
    viewModel: NoteViewModel,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionIcons(
            R.drawable.createpage,
            "create page"
        ) {
            viewModel.addPage(currentPage)
        }
        OptionIcons(
            R.drawable.bookmark_off,
            "bookmark"
        ) {

        }
        OptionIcons(
            R.drawable.ellipsis_horizontal,
            "setting"
        ) {

        }
    }
}

@Composable
fun OptionIcons(
    @DrawableRes resId: Int,
    desc: String,
    onClick: () -> Unit
) {
    Image(
        painterResource(id = resId),
        contentDescription = desc,
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
    )
}