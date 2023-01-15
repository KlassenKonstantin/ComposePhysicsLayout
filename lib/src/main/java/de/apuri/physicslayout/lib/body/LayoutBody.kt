package de.apuri.physicslayout.lib.body

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.WorldBody
import de.apuri.physicslayout.lib.shape.SizeAwareShape
import de.apuri.physicslayout.lib.shape.isCircle
import de.apuri.physicslayout.lib.shape.isCutCornerRectangle
import de.apuri.physicslayout.lib.shape.isRectangle
import de.apuri.physicslayout.lib.shape.isRoundedCornerRectangle
import de.apuri.physicslayout.lib.shape.toCircleShape
import de.apuri.physicslayout.lib.shape.toCutCornerRectangleShape
import de.apuri.physicslayout.lib.shape.toGenericShape
import de.apuri.physicslayout.lib.shape.toRectangleShape
import de.apuri.physicslayout.lib.shape.toRoundedRectangleShape
import de.apuri.physicslayout.lib.toWorldSize
import de.apuri.physicslayout.lib.toWorldVector2

data class LayoutBody(
    val id: String,
    override val width: Int,
    override val height: Int,
    override val shape: Shape,
    val isStatic: Boolean,
    val initialTranslation: Offset
) : SizeAwareShape

context(Simulation)
internal fun LayoutBody.toWorldBody() = WorldBody(
    id = id,
    width = width.toWorldSize(),
    height = height.toWorldSize(),
    shape = toWorldShape(),
    isStatic = isStatic,
    initialTranslation = initialTranslation.toWorldVector2()
)

context(Simulation)
internal fun LayoutBody.toWorldShape() = when {
    shape.isCircle() -> toCircleShape()
    shape.isRectangle() -> toRectangleShape()
    shape.isRoundedCornerRectangle() -> toRoundedRectangleShape()
    shape.isCutCornerRectangle() -> toCutCornerRectangleShape()
    else -> toGenericShape()
}