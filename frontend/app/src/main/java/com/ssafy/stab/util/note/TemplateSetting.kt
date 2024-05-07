package com.ssafy.stab.util.note

import com.ssafy.stab.R
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.Direction
import com.ssafy.stab.data.note.TemplateType

fun getTemplate(
    templateType: TemplateType,
    backgroundColor: BackgroundColor,
    direction: Direction
):Int {
    return when (templateType) {
        TemplateType.Plain -> when (backgroundColor) {
            BackgroundColor.White -> when (direction) {
                Direction.Portrait -> R.drawable.plain_white_portrait
                Direction.Landscape -> R.drawable.plain_white_landscape
            }

            BackgroundColor.Yellow -> when (direction) {
                Direction.Portrait -> R.drawable.plain_yellow_portrait
                Direction.Landscape -> R.drawable.plain_yellow_landscape
            }
        }

        TemplateType.Lined -> when (backgroundColor) {
            BackgroundColor.White -> when (direction) {
                Direction.Portrait -> R.drawable.lined_white_portrait
                Direction.Landscape -> R.drawable.lined_white_landscape
            }

            BackgroundColor.Yellow -> when (direction) {
                Direction.Portrait -> R.drawable.lined_yellow_portrait
                Direction.Landscape -> R.drawable.lined_yellow_landscape
            }
        }

        TemplateType.Grid -> when (backgroundColor) {
            BackgroundColor.White -> when (direction) {
                Direction.Portrait -> R.drawable.grid_white_portrait
                Direction.Landscape -> R.drawable.grid_white_landscape

            }

            BackgroundColor.Yellow -> when (direction) {
                Direction.Portrait -> R.drawable.grid_yellow_portrait
                Direction.Landscape -> R.drawable.grid_yellow_landscape
            }
        }
    }
}
