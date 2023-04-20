package de.apuri.physicslayout.lib2

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import org.dyn4j.geometry.Vector2

private const val PATH_SEGMENTS = 1001

internal class LayoutToSimulation(
    private val density: Density,
    private val scale: Double
) {
    // TODO Comment!
    lateinit var containerLayoutCoordinates: LayoutCoordinates

//    fun convertBody(coordinates: LayoutCoordinates, shape: Shape): SimulationBody {
//        val layoutWidth = coordinates.size.width
//        val layoutHeight = coordinates.size.height
//
//        val simulationWidth = layoutWidth.toSimulationSize()
//        val simulationHeight = layoutHeight.toSimulationSize()
//    }

    fun convertBorder(size: IntSize, shape: Shape?) = SimulationBorder(
        width = size.width.toSimulationSize(),
        height = size.height.toSimulationSize(),
        shape = shape.toSimulationShape(size)
    ).also {
        Log.d(TAG, "New border: $it")
    }

    private fun Int.toSimulationSize() = this / scale

    private fun Float.toSimulationSize() = this / scale

    private fun Offset.toWorldVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)

    private fun List<Offset>.toVector2() = map {
        it.toWorldVector2()
    }

    private fun Shape?.toSimulationShape(size: IntSize) = when {
        this == null -> null
        !isSupported() -> throw IllegalArgumentException("${this::class.simpleName} is not supported")
        isCircle() -> SimulationShape.Circle(size.width.toSimulationSize() / 2)
        isRectangle() -> SimulationShape.Rectangle(
            size.width.toSimulationSize(),
            size.height.toSimulationSize()
        )
        isRoundedCornerRectangle() -> SimulationShape.RoundedCornerRectangle(
            width = size.width.toSimulationSize(),
            height = size.height.toSimulationSize(),
            cornerRadius = (this as RoundedCornerShape).toRadius(
                size.width.toFloat(),
                size.height.toFloat(),
                density
            ).toSimulationSize()
        )
        else -> SimulationShape.Generic(
            toPoints(
                Size(size.width.toFloat(), size.height.toFloat()),
                LayoutDirection.Ltr,
                density,
                PATH_SEGMENTS
            ).toVector2()
        )
    }
}