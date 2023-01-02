package de.apuri.physicslayout.lib.shape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.layout.LayoutBody
import de.apuri.physicslayout.lib.toWorldSize

sealed class BodyShape {
    object Circle: BodyShape()
    object Rectangle: BodyShape()
    data class RoundedCornerRect(
        val radius: Double
    ): BodyShape()
}

context(Simulation)
internal fun LayoutBody.toBodyShape() = when {
    shape.isCircle() -> BodyShape.Circle
    shape.isRectangle() -> BodyShape.Rectangle
    else -> BodyShape.RoundedCornerRect(
        shape.topStart.toPx(
            Size(width.toFloat(), height.toFloat()),
            density
        ).toWorldSize()
    )
}

private fun RoundedCornerShape.isCircle() = this == CircleShape
private fun RoundedCornerShape.isRectangle() =
    this == RoundedCornerShape(0.dp) ||
            this == RoundedCornerShape(0) ||
            this == RoundedCornerShape(0f)