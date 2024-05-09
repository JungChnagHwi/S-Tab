package com.ssafy.stab.data.note.request

import com.ssafy.stab.data.note.BackgroundColor
import com.ssafy.stab.data.note.TemplateType

data class NoteRequest(
    val parentFolderId: String,
    val title: String,
    val color: BackgroundColor,
    val template: TemplateType,
    val direction: Int
)