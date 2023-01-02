package de.apuri.physicslayout.lib.drag

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.toWorldVector2

@Immutable
data class TouchEvent(
    val pointerId: Long,
    val localOffset: Offset,
    val type: TouchType,
)

enum class TouchType {
    DOWN, MOVE, UP
}

context(Simulation)
internal fun TouchEvent.toWorldTouchEvent() = WorldTouchEvent(
    pointerId = pointerId,
    localOffset = localOffset.toWorldVector2(),
    type = type
)