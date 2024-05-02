package com.ssafy.stab.components.note

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.NoteController
import com.ssafy.stab.util.note.data.PenType

@Composable
fun ControlsBar(
    noteController: NoteController,
    undoAvailable: MutableState<Boolean>,
    redoAvailable: MutableState<Boolean>,
) {
    Row(modifier = Modifier.padding(10.dp).background(Background), verticalAlignment = Alignment.CenterVertically) {
        EditIcons(
            if (undoAvailable.value) R.drawable.undo_abled else R.drawable.undo_disabled,
            "undo",
            ) {
                if (undoAvailable.value) noteController.undo()
            }
        EditIcons(
            if (redoAvailable.value) R.drawable.redo_abled else R.drawable.redo_disabled,
            "redo"
            ) {
                if (redoAvailable.value) noteController.redo()
            }
        EditIcons(
            if (noteController.penType == PenType.Pen) R.drawable.pen_abled else R.drawable.pen_disabled,
            "pen"
        ) {
            noteController.changePenType(PenType.Pen)
        }
        EditIcons(
            if (noteController.penType == PenType.Highlighter) R.drawable.highliter_abled else R.drawable.highliter_disabled,
            "highliter"
        ) {
            noteController.changePenType(PenType.Highlighter)
        }
        EditIcons(
            if (noteController.penType == PenType.Eraser) R.drawable.eraser_abled else R.drawable.eraser_disabled,
            "eraser"
        ) {
            noteController.changePenType(PenType.Eraser)
        }
        EditIcons(
            if (noteController.penType == PenType.Lasso) R.drawable.lasso_abled else R.drawable.lasso_disabled,
            "lasso"
        ) {
            noteController.changePenType(PenType.Lasso)
        }
        EditIcons(
            R.drawable.image_abled,
            "insert image"
        ) {

        }
    }
}

@Composable
fun RowScope.EditIcons(
    @DrawableRes resId:  Int,
    desc: String,
    onClick: () -> Unit
) {
    val modifier = Modifier.size(40.dp)
    IconButton(onClick = onClick, modifier = modifier) {
        Image(
            painterResource(id = resId),
            contentDescription = desc,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
            )
    }
}