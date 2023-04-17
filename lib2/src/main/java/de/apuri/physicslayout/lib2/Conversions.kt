package de.apuri.physicslayout.lib2

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.dyn4j.geometry.Vector2

private const val PATH_SEGMENTS = 1001

context(ScaleAware)
internal fun Int.toWorldSize() = this / scale

context(ScaleAware)
internal fun Float.toWorldSize() = this / scale

context(ScaleAware)
internal fun Double.toLayoutSize() = (this * scale).toFloat()

context(ScaleAware)
fun List<Offset>.toVector2() = map {
    it.toWorldVector2()
}

context(ScaleAware)
internal fun Offset.toWorldVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)

context(ScaleAware)
internal fun PhysicsEntity.toWorldShape() = when {
    shape.isCircle() -> toCircleShape()
    shape.isRectangle() -> toRectangleShape()
    shape.isRoundedCornerRectangle() -> toGenericShape()
    else -> toGenericShape()
}

//context(ScaleAware)
//internal fun List<LayoutBody>.toWorldBodies() = map {
//    it.toWorldBody()
//}

fun Offset.toVector2() = Vector2(x.toDouble(), y.toDouble())

context(ScaleAware)
private fun PhysicsEntity.toCircleShape() = WorldShape.Circle(width.toWorldSize() / 2)

context(ScaleAware)
private fun PhysicsEntity.toRectangleShape() = WorldShape.Rectangle(width.toWorldSize(), height.toWorldSize())

context(ScaleAware)
private fun PhysicsEntity.toRoundedRectangleShape(): WorldShape.RoundedCornerRectangle {
    return WorldShape.RoundedCornerRectangle(
        width = width.toWorldSize(),
        height = height.toWorldSize(),
        cornerRadius = (shape as RoundedCornerShape).toRadius(width.toFloat(), height.toFloat(), density).toWorldSize()
    )
}

context(ScaleAware)
private fun PhysicsEntity.toCutCornerRectangleShape(): WorldShape.CutCornerRectangle {
    return WorldShape.CutCornerRectangle(
        width = width.toWorldSize(),
        height = height.toWorldSize(),
        cutLength = (shape as CutCornerShape).toCutLength(width.toFloat(), height.toFloat(), density).toWorldSize()
    )
}

context(ScaleAware)
private fun PhysicsEntity.toGenericShape() = WorldShape.Generic(
    shape.toPoints(Size(width.toFloat(), height.toFloat()), androidx.compose.ui.unit.LayoutDirection.Ltr, density, PATH_SEGMENTS).toVector2()
)

context(ScaleAware)
internal fun WorldBody.toLayoutTransformation(): LayoutTransformation {
    val lx = transform.translationX - offset.x
    val ly = transform.translationY - offset.y

    return LayoutTransformation(
        translationX = lx.toLayoutSize(),
        translationY = ly.toLayoutSize(),
        rotation = transform.rotation.toDegrees().toFloat(),
    )
}