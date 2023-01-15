package de.apuri.physicslayout.lib.shape

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.body.LayoutBody
import de.apuri.physicslayout.lib.body.toWorldBody
import de.apuri.physicslayout.lib.toVector2
import de.apuri.physicslayout.lib.toWorldSize

private const val PATH_SEGMENTS = 1001

interface SizeAwareShape {
    val width: Int
    val height: Int
    val shape: Shape
}

context(Simulation)
internal fun List<LayoutBody>.toWorldBodies() = map {
    it.toWorldBody()
}

context(Simulation)
internal fun SizeAwareShape.toCircleShape() = WorldShape.Circle(width.toWorldSize() / 2)

context(Simulation)
internal fun SizeAwareShape.toRectangleShape() = WorldShape.Rectangle(width.toWorldSize(), height.toWorldSize())

context(Simulation)
internal fun SizeAwareShape.toRoundedRectangleShape(): WorldShape.RoundedCornerRectangle {
    return WorldShape.RoundedCornerRectangle(
        width = width.toWorldSize(),
        height = height.toWorldSize(),
        cornerRadius = (shape as RoundedCornerShape).toRadius(width.toFloat(), height.toFloat(), density).toWorldSize()
    )
}

context(Simulation)
internal fun SizeAwareShape.toCutCornerRectangleShape(): WorldShape.CutCornerRectangle {
    return WorldShape.CutCornerRectangle(
        width = width.toWorldSize(),
        height = height.toWorldSize(),
        cutLength = (shape as CutCornerShape).toCutLength(width.toFloat(), height.toFloat(), density).toWorldSize()
    )
}

context(Simulation)
internal fun SizeAwareShape.toGenericShape() = WorldShape.Generic(
    shape.toPoints(Size(width.toFloat(), height.toFloat()), LayoutDirection.Ltr, density, PATH_SEGMENTS).toVector2()
)