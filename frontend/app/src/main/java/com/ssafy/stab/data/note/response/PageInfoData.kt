package com.ssafy.stab.data.note.response

import com.google.gson.annotations.SerializedName
import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType

data class NewPage(
    @SerializedName("pageId")
    val pageId: String,
    @SerializedName("template")
    val template: TemplateType,
    @SerializedName("color")
    val color: BackgroundColor,
    @SerializedName("direction")
    val direction: Int
)