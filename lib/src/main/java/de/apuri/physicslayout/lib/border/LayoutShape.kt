package de.apuri.physicslayout.lib.border

import androidx.compose.ui.graphics.Shape
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.shape.SizeAwareShape
import de.apuri.physicslayout.lib.shape.isCircle
import de.apuri.physicslayout.lib.shape.isRectangle
import de.apuri.physicslayout.lib.shape.isRoundedCornerRectangle
import de.apuri.physicslayout.lib.shape.toCircleShape
import de.apuri.physicslayout.lib.shape.toGenericShape
import de.apuri.physicslayout.lib.shape.toRectangleShape

internal data class LayoutShape(
    override val width: Int,
    override val height: Int,
    override val shape: Shape
) : SizeAwareShape

context(Simulation)
internal fun LayoutShape.toWorldShape() = when {
    shape.isCircle() -> toCircleShape()
    shape.isRectangle() -> toRectangleShape()
    shape.isRoundedCornerRectangle() -> toGenericShape()
    else -> toGenericShape()
}