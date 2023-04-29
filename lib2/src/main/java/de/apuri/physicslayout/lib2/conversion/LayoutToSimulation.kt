package de.apuri.physicslayout.lib2.conversion

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import de.apuri.physicslayout.lib2.BodyConfig
import de.apuri.physicslayout.lib2.TAG
import de.apuri.physicslayout.lib2.drag.LayoutTouchEvent
import de.apuri.physicslayout.lib2.isCircle
import de.apuri.physicslayout.lib2.isRectangle
import de.apuri.physicslayout.lib2.isRoundedCornerRectangle
import de.apuri.physicslayout.lib2.isSupported
import de.apuri.physicslayout.lib2.simulation.SimulationBody
import de.apuri.physicslayout.lib2.simulation.SimulationBorder
import de.apuri.physicslayout.lib2.simulation.SimulationShape
import de.apuri.physicslayout.lib2.simulation.SimulationTouchEvent
import de.apuri.physicslayout.lib2.toPoints
import de.apuri.physicslayout.lib2.toRadius
import org.dyn4j.geometry.Vector2

private const val PATH_SEGMENTS = 1001

internal class LayoutToSimulation(
    private val density: Density,
    private val scale: Double
) {
    lateinit var containerLayoutCoordinates: LayoutCoordinates

    fun convertBody(
        coordinates: LayoutCoordinates,
        shape: Shape,
        bodyConfig: BodyConfig
    ): Pair<SimulationBody, Offset> {
        /**
         * Width and height of the composable
         */
        val (lw, lh) = coordinates.size

        /**
         * Width and height of the [PhysicsLayout]
         */
        val (bw, bh) = containerLayoutCoordinates.size

        /**
         * Half width and height of the [PhysicsLayout]
         */
        val (bwh, bhh) = IntSize(bw / 2, bh / 2)

        /**
         * The local position of the composable in the [LayoutCoordinates] of the [PhysicsLayout]
         */
        val (lx, ly) = containerLayoutCoordinates.localPositionOf(
            coordinates.parentCoordinates!!,
            coordinates.positionInParent()
        )

        /**
         * Position of the composable with the origin in the center of the [PhysicsLayout].
         * We need that because the world in the physics engine has its origin in the center.
         */
        val positionFromCenter = Offset(
            (lx * bwh - lx * -bwh + bw * -bwh) / bw + lw / 2,
            (ly * bhh - ly * -bhh + bh * -bhh) / bh + lh / 2
        )

        Log.d("asdf", "${positionFromCenter}")

        return SimulationBody(
            width = lw.toSimulationSize(),
            height = lh.toSimulationSize(),
            shape = shape.toSimulationShape(coordinates.size)!!,
            initialOffset = positionFromCenter.toSimulationVector2(),
            bodyConfig = bodyConfig,
        ) to positionFromCenter
    }

    fun convertTouchEvent(touchEvent: LayoutTouchEvent) = SimulationTouchEvent(
        pointerId = touchEvent.pointerId,
        offset = touchEvent.offset.toSimulationVector2(),
        type = touchEvent.type
    )

    fun convertBorder(size: IntSize, shape: Shape?) = SimulationBorder(
        width = size.width.toSimulationSize(),
        height = size.height.toSimulationSize(),
        shape = shape.toSimulationShape(size)
    ).also {
        Log.d(TAG, "New border: $it")
    }

    private fun Int.toSimulationSize() = this / scale

    private fun Float.toSimulationSize() = this / scale

    fun Offset.toSimulationVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)

    private fun List<Offset>.toVector2() = map {
        it.toSimulationVector2()
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