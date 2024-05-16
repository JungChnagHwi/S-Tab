package com.ssafy.stab.util.note

import android.util.Log
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.ssafy.stab.data.note.MotionEvent

suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onTouchEVent: (MotionEvent, PointerInputChange) -> Unit
) {
    val down: PointerInputChange = awaitFirstDown()
    onTouchEVent(MotionEvent.Down, down)

    var pointer = down

    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) {
            change: PointerInputChange, overSlop: Offset ->
            // consume()이 호출돼야 드래그가 작동
            change.consume()
        }

    if (change != null) {
        drag(change.id) { pointerInputChange ->
            pointer = pointerInputChange
            onTouchEVent(MotionEvent.Move, pointer)
        }

        onTouchEVent(MotionEvent.Up, pointer)
    } else {
        onTouchEVent(MotionEvent.Up, pointer)
    }

}

fun Modifier.dragMotionEvent(
    onTouchEVent: (MotionEvent, PointerInputChange) -> Unit
) = this.then(
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitDragMotionEvent(onTouchEVent)
            }
        }
    )

suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) {
    val down: PointerInputChange = awaitFirstDown()
    onDragStart(down)

    var pointer = down

    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) {
            change: PointerInputChange, over: Offset ->
            if (change.type == PointerType.Stylus) {
                change.consume()
            }
            Log.d("ok", "${over.x} ${over.y}")
        }

    if (change != null) {
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            Log.d("pointer", "${pointer.position}")
            onDrag(pointer)
        }

        onDragEnd(pointer)
    } else {
        onDragEnd(pointer)
        Log.d("no", "k")
    }
}

fun Modifier.dragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) = this.then(
    Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitDragMotionEvent(onDragStart, onDrag, onDragEnd)
        }
    }
)