package de.apuri.physicslayout.lib2

import org.dyn4j.geometry.Vector2

internal sealed class SimulationShape {
    data class Circle(
        val radius: Double
    ) : SimulationShape()

    data class Rectangle(
        val width: Double,
        val height: Double,
    ) : SimulationShape()

    data class RoundedCornerRectangle(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    ) : SimulationShape()

    data class CutCornerRectangle(
        val width: Double,
        val height: Double,
        val cutLength: Double
    ) : SimulationShape()

    data class Generic(
        val vertices: List<Vector2>
    ) : SimulationShape()
}