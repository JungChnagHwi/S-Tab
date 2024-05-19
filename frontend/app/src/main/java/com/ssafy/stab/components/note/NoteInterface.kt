package com.ssafy.stab.components.note

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.screens.note.NoteViewModel

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun PageInterfaceBar(
    currentPage: Int,
    viewModel: NoteViewModel,
) {
    val isBookmarked = viewModel.isBookmarked.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionIcons(
            R.drawable.createpage,
            "create page"
        ) {
            viewModel.addPage(currentPage)
        }
        OptionIcons(
            if (isBookmarked.value) R.drawable.bookmark_on else R.drawable.bookmark_off,
            "bookmark"
        ) {
            if (isBookmarked.value) viewModel.deleteLikePage(currentPage) else viewModel.addLikePage(currentPage)
        }
        var expanded by remember { mutableStateOf(false) }
//        OptionIcons(
//            R.drawable.export,
//            "export"
//        ) {
//            expanded = true
//        }
        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text="이미지로 저장하기 (.png)")
                },
                onClick = { 
                    expanded = false
                    // 이미지 저장하기
                    // saveAsImage()
                })

        }
//        OptionIcons(
//            R.drawable.ellipsis_horizontal,
//            "setting"
//        ) {
//
//        }
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