package de.apuri.physicslayout.lib.shape

import android.graphics.PathMeasure
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal fun Shape.toPoints(size: Size, layoutDirection: LayoutDirection, density: Density, steps: Int): List<Offset> {
    val outline = createOutline(size, layoutDirection, density)
    val path = Path().apply { addOutline(outline) }
    if (!path.isConvex) {
        throw IllegalArgumentException("Only convex shapes are supported")
    }
    val pm = PathMeasure().apply { setPath(path.asAndroidPath(), true) }
    val stepSize = pm.length / steps
    val coordinates = FloatArray(2)
    val offset = Offset(size.width / 2, size.height / 2)
    return buildList {
        (0 until steps).forEach {
            pm.getPosTan(it * stepSize, coordinates, null)
            add(Offset(coordinates[0], coordinates[1]) - offset)
        }
    }.let {
        // Points come out in reversed order for RoundedCornerShape?
        if (this is RoundedCornerShape) it.reversed() else it
    }
}

internal fun RoundedCornerShape.toRadius(width: Float, height: Float, density: Density): Float {
    return topStart.toPx(
        Size(width, height),
        density
    )
}

internal fun CutCornerShape.toCutLength(width: Float, height: Float, density: Density): Float {
    return topStart.toPx(
        Size(width, height),
        density
    )
}

internal fun Shape.isSupported() = isCircle() || isRectangle() || isRoundedCornerRectangle() || isCutCornerRectangle()
internal fun Shape.isCircle() = this == CircleShape
internal fun Shape.isRectangle() = this == RectangleShape
internal fun Shape.isRoundedCornerRectangle() = this is RoundedCornerShape
internal fun Shape.isCutCornerRectangle() = this is CutCornerShape