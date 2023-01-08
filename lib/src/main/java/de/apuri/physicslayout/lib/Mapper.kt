package de.apuri.physicslayout.lib

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Vector2

context(Simulation)
fun List<Offset>.toVector2() = map {
    it.toWorldVector2()
}
fun Offset.toVector2() = Vector2(x.toDouble(), y.toDouble())

context(Simulation)
internal fun Offset.toWorldVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)

context(Simulation)
internal fun Body.toLayoutTransformation() = LayoutTransformation(
    translationX = transform.translationX.toLayoutSize(),
    translationY = transform.translationY.toLayoutSize(),
    rotation = transform.rotation.toDegrees().toFloat(),
)

context(Simulation)
internal fun Int.toWorldSize() = this / scale

context(Simulation)
internal fun Dp.toWorldSize() = with(density) { toPx() } / scale

context(Simulation)
internal fun Float.toWorldSize() = this / scale

context(Simulation)
internal fun Double.toLayoutSize() = (this * scale).toFloat()