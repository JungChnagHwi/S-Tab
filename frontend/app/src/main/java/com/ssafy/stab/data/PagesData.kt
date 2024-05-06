package com.ssafy.stab.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.ssafy.stab.util.note.data.PathInfo
import java.time.LocalDateTime

data class Page(
    val pageId: String,
    val color: String,
    val template: String,
    val isBookmarked: Boolean,
    val pdfUrl: String,
    val pdfPage: Int,
    val updatedAt: LocalDateTime,
    val paths: MutableList<PathInfo>,
    val figures: List<Any>,
    val textBoxes: List<Any>,
    val images: List<Any>
)

data class PagesResponse (
    val data: MutableList<Int>
)
