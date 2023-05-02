package de.apuri.physicslayout.lib2.drag

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.touch(
    onTouchEvent: (LayoutTouchEvent) -> Unit
) = pointerInput(Unit) {
    val center = Offset(size.width / 2f, size.height / 2f)
    awaitEachGesture {
        val changeAfterSlop = awaitTouchSlopOrCancellation(
            awaitPointerEvent().changes.first().id,
        ) { change, _ ->
            change.consume()
        }

        fun handlePointerInputChange(change: PointerInputChange) {
            val type = when {
                change.changedToDown() -> TouchType.DOWN
                change.changedToUp() -> TouchType.UP
                else -> TouchType.MOVE
            }

            onTouchEvent(
                LayoutTouchEvent(
                    pointerId = change.id.value,
                    offset = change.position - center,
                    type = type,
                )
            )
        }

        if (changeAfterSlop != null) {
            handlePointerInputChange(changeAfterSlop)
            do {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    handlePointerInputChange(change)
                }
            } while (!event.changes.all { it.changedToUp() })
        }
    }
}