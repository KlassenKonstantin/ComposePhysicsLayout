package de.apuri.physicslayout.lib.conversion

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import de.apuri.physicslayout.lib.BodyConfig
import de.apuri.physicslayout.lib.drag.LayoutTouchEvent
import de.apuri.physicslayout.lib.isCircle
import de.apuri.physicslayout.lib.isRectangle
import de.apuri.physicslayout.lib.isRoundedCornerRectangle
import de.apuri.physicslayout.lib.isSupported
import de.apuri.physicslayout.lib.simulation.SimulationBody
import de.apuri.physicslayout.lib.simulation.SimulationBorder
import de.apuri.physicslayout.lib.simulation.SimulationShape
import de.apuri.physicslayout.lib.simulation.SimulationTouchEvent
import de.apuri.physicslayout.lib.toPoints
import de.apuri.physicslayout.lib.toRadius
import org.dyn4j.geometry.Vector2

/**
 * The number of path segments a generic shape should consist of
 */
private const val PATH_SEGMENTS = 100

/**
 * Handles transformations from layout to simulation space
 */
@Stable
internal class LayoutToSimulation(
    private val density: Density,
    private val scale: Double,
) {

    var containerLayoutCoordinates = mutableStateOf<LayoutCoordinates?>(null)

    fun convertBody(
        coordinates: LayoutCoordinates,
        shape: Shape,
        bodyConfig: BodyConfig
    ): Pair<SimulationBody, Offset> = containerLayoutCoordinates.value?.let { containerLayoutCoordinates ->
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

        SimulationBody(
            width = lw.toSimulationSize(),
            height = lh.toSimulationSize(),
            shape = shape.toSimulationBodyShape(coordinates.size),
            initialOffset = positionFromCenter.toSimulationVector2(),
            bodyConfig = bodyConfig,
        ) to positionFromCenter
    } ?: throw IllegalStateException()

    fun convertTouchEvent(touchEvent: LayoutTouchEvent) = SimulationTouchEvent(
        pointerId = touchEvent.pointerId,
        offset = touchEvent.offset.toSimulationVector2(),
        type = touchEvent.type
    )

    fun convertBorder(size: IntSize, shape: Shape?) = SimulationBorder(
        width = size.width.toSimulationSize(),
        height = size.height.toSimulationSize(),
        shape = shape.toSimulationBorderShape(size)
    )

    private fun Int.toSimulationSize() = this / scale

    private fun Float.toSimulationSize() = this / scale

    private fun Offset.toSimulationVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)

    private fun List<Offset>.toVector2() = map {
        it.toSimulationVector2()
    }

    private fun Shape.toSimulationBodyShape(size: IntSize) = when {
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

    private fun Shape?.toSimulationBorderShape(size: IntSize) = when {
        this == null -> null
        !isSupported() -> throw IllegalArgumentException("${this::class.simpleName} is not supported")
        isCircle() -> SimulationShape.Circle(size.width.toSimulationSize() / 2)
        isRectangle() -> SimulationShape.Rectangle(
            size.width.toSimulationSize(),
            size.height.toSimulationSize()
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

internal val LocalLayoutToSimulation = staticCompositionLocalOf<LayoutToSimulation> {
    throw IllegalStateException("No LayoutToSimulation provided")
}