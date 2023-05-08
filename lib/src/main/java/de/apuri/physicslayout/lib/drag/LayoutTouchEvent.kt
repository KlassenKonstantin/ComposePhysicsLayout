package de.apuri.physicslayout.lib.drag

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class LayoutTouchEvent(
    val pointerId: Long,
    val offset: Offset,
    val type: TouchType,
)

@Immutable
enum class TouchType {
    DOWN, MOVE, UP
}