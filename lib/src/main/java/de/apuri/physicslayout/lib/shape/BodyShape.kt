package de.apuri.physicslayout.lib.shape

import android.graphics.PathMeasure
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.layout.LayoutBody
import de.apuri.physicslayout.lib.toVector2
import de.apuri.physicslayout.lib.toWorldSize
import org.dyn4j.geometry.Vector2

private const val STEPS = 100

internal sealed class BodyShape {
    data class Circle(
        val radius: Double
    ) : BodyShape()

    data class Rectangle(
        val width: Double,
        val height: Double,
    ) : BodyShape()

    data class RoundedRectangle(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    ) : BodyShape()

    data class Generic(
        val vertices: List<Vector2>
    ) : BodyShape()
}

context(Simulation)
internal fun LayoutBody.toBodyShape() = when {
    shape.isCircle() -> toCircleShape()
    shape.isRectangle() -> toRectangleShape()
    shape.isRoundedRectangle() -> toRoundedRectangleShape()
    else -> toGenericShape()
}

context(Simulation)
private fun LayoutBody.toCircleShape() = BodyShape.Circle(width.toWorldSize() / 2)

context(Simulation)
private fun LayoutBody.toRectangleShape() = BodyShape.Rectangle(width.toWorldSize(), height.toWorldSize())

context(Simulation)
private fun LayoutBody.toRoundedRectangleShape(): BodyShape.RoundedRectangle {
    return BodyShape.RoundedRectangle(
        width = width.toWorldSize(),
        height = height.toWorldSize(),
        cornerRadius = (shape as RoundedCornerShape).toRadius(width.toFloat(), height.toFloat(), density).toWorldSize()
    )
}

context(Simulation)
private fun LayoutBody.toGenericShape() = BodyShape.Generic(
    shape.toPoints(Size(width.toFloat(), height.toFloat()), LayoutDirection.Ltr, density, STEPS).toVector2()
)

private fun Shape.toPoints(size: Size, layoutDirection: LayoutDirection, density: Density, steps: Int): List<Offset> {
    val outline = createOutline(size, layoutDirection, density)
    val path = Path().apply { addOutline(outline) }
    val pm = PathMeasure().apply { setPath(path.asAndroidPath(), true) }
    val stepSize = pm.length / steps
    val coordinates = FloatArray(2)
    val offset = Offset(size.width / 2, size.height / 2)
    return buildList {
        (0 until steps).forEach {
            pm.getPosTan(it * stepSize, coordinates, null)
            add(Offset(coordinates[0], coordinates[1]) - offset)
        }
    }
}

private fun RoundedCornerShape.toRadius(width: Float, height: Float, density: Density): Float {
    return topStart.toPx(
        Size(width, height),
        density
    )
}

private fun Shape.isCircle() = this == CircleShape
private fun Shape.isRectangle() =
    this == RoundedCornerShape(0.dp) ||
            this == RoundedCornerShape(0) ||
            this == RoundedCornerShape(0f)

private fun Shape.isRoundedRectangle() = this is RoundedCornerShape