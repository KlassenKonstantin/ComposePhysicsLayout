package de.apuri.physicslayout.lib

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput

@Immutable
data class TouchEvent(
    val pointerId: Long,
    val localOffset: Offset,
    val type: TouchType
)

enum class TouchType {
    DOWN, MOVE, UP
}

fun Modifier.touch(
    onTouchEvent: (TouchEvent) -> Unit
) = pointerInput(Unit) {
    val center = Offset(size.width / 2f, size.height / 2f)
    forEachGesture {
        awaitPointerEventScope {
            val firstDown = awaitFirstDown()
            val localOffset = firstDown.position - center
            onTouchEvent(TouchEvent(firstDown.id.value, localOffset, TouchType.DOWN))
            do {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    val type = when {
                        change.changedToDown() -> TouchType.DOWN
                        change.changedToUp() -> TouchType.UP
                        else -> TouchType.MOVE
                    }
                    onTouchEvent(TouchEvent(change.id.value, change.position - center, type))
                }
            } while (!event.changes.all { it.changedToUp() })
        }
    }
}