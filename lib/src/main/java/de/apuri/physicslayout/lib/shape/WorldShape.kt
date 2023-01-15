package de.apuri.physicslayout.lib.shape

import org.dyn4j.geometry.Vector2

internal sealed class WorldShape {
    data class Circle(
        val radius: Double
    ) : WorldShape()

    data class Rectangle(
        val width: Double,
        val height: Double,
    ) : WorldShape()

    data class RoundedCornerRectangle(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    ) : WorldShape()

    data class CutCornerRectangle(
        val width: Double,
        val height: Double,
        val cutLength: Double
    ) : WorldShape()

    data class Generic(
        val vertices: List<Vector2>
    ) : WorldShape()
}