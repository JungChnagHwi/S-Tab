package com.ssafy.stab.components.note

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun Template(
    @DrawableRes resId: Int,
    modifier: Modifier
) {
    Image(
        painterResource(id = resId),
        contentDescription = null,
        modifier = modifier
    )
}