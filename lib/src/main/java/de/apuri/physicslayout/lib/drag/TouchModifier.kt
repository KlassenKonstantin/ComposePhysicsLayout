package de.apuri.physicslayout.lib.drag

import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.touch(
    onTouchEvent: (TouchEvent) -> Unit
) = pointerInput(Unit) {
    val center = Offset(size.width / 2f, size.height / 2f)
    forEachGesture {
        awaitPointerEventScope {
            do {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    val type = when {
                        change.changedToDown() -> TouchType.DOWN
                        change.changedToUp() -> TouchType.UP
                        else -> TouchType.MOVE
                    }

                    change.consume()

                    onTouchEvent(
                        TouchEvent(
                            pointerId = change.id.value,
                            localOffset = change.position - center,
                            type = type,
                        )
                    )
                }
            } while (!event.changes.all { it.changedToUp() })
        }
    }
}