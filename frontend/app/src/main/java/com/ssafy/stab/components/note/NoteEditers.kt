package com.ssafy.stab.components.note

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.stab.R
import com.ssafy.stab.data.PreferencesUtil
import com.ssafy.stab.data.note.PenType
import com.ssafy.stab.util.note.NoteControlViewModel

@Composable
fun ControlsBar(
    viewModel: NoteControlViewModel,
) {
    val undoAvailable by viewModel.undoAvailable.collectAsState()
    val redoAvailable by viewModel.redoAvailable.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditIcons(
            if (undoAvailable) R.drawable.undo_abled else R.drawable.undo_disabled,
            "undo"
            ) {
                if (undoAvailable) viewModel.undo(PreferencesUtil.getLoginDetails().userName ?: "")
            }
        EditIcons(
            if (redoAvailable) R.drawable.redo_abled else R.drawable.redo_disabled,
            "redo"
            ) {
                if (redoAvailable) viewModel.redo()
            }
        Spacer(modifier = Modifier.width(4.dp))
        Divider(
            color = Color(0xFFCCD7ED),
            modifier = Modifier
                .height(28.dp)
                .width(2.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        PenIcons(
            if (viewModel.penType == PenType.Pen) R.drawable.pen_abled else R.drawable.pen_disabled,
            "pen", PenType.Pen, viewModel
        ) {
            viewModel.changePenType(PenType.Pen)
        }
        PenIcons(
            if (viewModel.penType == PenType.Highlighter) R.drawable.highliter_abled else R.drawable.highliter_disabled,
            "highliter", PenType.Highlighter, viewModel
        ) {
            viewModel.changePenType(PenType.Highlighter)
        }
        PenIcons(
            if (viewModel.penType == PenType.Eraser) R.drawable.eraser_abled else R.drawable.eraser_disabled,
            "eraser", PenType.Eraser, viewModel
        ) {
            viewModel.changePenType(PenType.Eraser)
        }
//        PenIcons(
//            if (viewModel.penType == PenType.Lasso) R.drawable.lasso_abled else R.drawable.lasso_disabled,
//            "lasso", PenType.Lasso, viewModel
//        ) {
//            viewModel.changePenType(PenType.Lasso)
//        }
        EditIcons(
            R.drawable.image_abled,
            "insert image"
        ) {
            viewModel.changePenType(PenType.Image)
        }
    }
}

@Composable
fun EditIcons(
    @DrawableRes resId:  Int,
    desc: String,
    onClick: () -> Unit
) {
    val modifier = Modifier
        .size(32.dp)
        .padding(2.dp)
        .clickable { onClick() }

    Image(
        painterResource(id = resId),
        contentDescription = desc,
        modifier = modifier,
        )
}

@Composable
fun PenIcons(
    @DrawableRes resId:  Int,
    desc: String,
    penType: PenType,
    viewModel: NoteControlViewModel,
    onClick: () -> Unit
) {
    Box(
        modifier = if (penType == viewModel.penType) {
            Modifier
                .background(Color(0xFFBADAFF), shape = RoundedCornerShape(12.dp))
        } else {
            Modifier
        }
            .size(38.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(id = resId),
            contentDescription = desc,
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp)
        )
    }
}

@Composable
fun ColorOptions(
    viewModel: NoteControlViewModel
) {
    val color by viewModel.color.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.penType == PenType.Pen) {
            ColorIcons("000000", color) {
                viewModel.changeColor("000000")
            }
            ColorIcons("FF0000", color) {
                viewModel.changeColor("FF0000")
            }
            ColorIcons("0000FF", color) {
                viewModel.changeColor("0000FF")
            }
            ColorIcons("31BC47", color) {
                viewModel.changeColor("31BC47")
            }
        } else if (viewModel.penType == PenType.Highlighter) {
            ColorIcons("FFB800", color) {
                viewModel.changeColor("FFB800")
            }
            ColorIcons("FF0000", color) {
                viewModel.changeColor("FF0000")
            }
            ColorIcons("0000FF", color) {
                viewModel.changeColor("0000FF")
            }
            ColorIcons("31BC47", color) {
                viewModel.changeColor("31BC47")
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ColorIcons(
    colorTint: String,
    currentColor: String,
    onClick: () -> Unit
) {
    Box(
        modifier = if (colorTint == currentColor) {
            Modifier
                .clip(CircleShape).background(Color(0xFFBADAFF))
        } else {
            Modifier
        }
            .size(38.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(color = "FF$colorTint".toLong(16)))
        )
    }
}

@Composable
fun StrokeOptions(
    viewModel: NoteControlViewModel
) {
    val strokeWidth by viewModel.strokeWidth.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.penType == PenType.Pen) {
            StrokeWidthIcons(strokeWidth  = 4f, strokeWidth) {
                viewModel.changeStrokeWidth(4f)
            }
            StrokeWidthIcons(strokeWidth  = 10f, strokeWidth) {
                viewModel.changeStrokeWidth(10f)
            }
            StrokeWidthIcons(strokeWidth  = 16f, strokeWidth) {
                viewModel.changeStrokeWidth(16f)
            }
        } else if (viewModel.penType == PenType.Highlighter || viewModel.penType == PenType.Eraser) {
            StrokeWidthIcons(strokeWidth  = 16f, strokeWidth) {
                viewModel.changeStrokeWidth(16f)
            }
            StrokeWidthIcons(strokeWidth  = 20f, strokeWidth) {
                viewModel.changeStrokeWidth(20f)
            }
            StrokeWidthIcons(strokeWidth  = 28f, strokeWidth) {
                viewModel.changeStrokeWidth(28f)
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StrokeWidthIcons(
    strokeWidth : Float,
    currentWidth: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = if (strokeWidth == currentWidth) {
            Modifier.background(Color(0xFFBADAFF), shape = RoundedCornerShape(12.dp))
        } else {
            Modifier
        }
            .size(38.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = strokeWidth * 0.8f,
                cap = StrokeCap.Round,
                )
        }
    }
}