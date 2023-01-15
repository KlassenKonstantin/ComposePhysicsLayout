package de.apuri.physicslayout.lib.border

import de.apuri.physicslayout.lib.shape.WorldShape
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry

internal fun createWorldBorderFixtures(
    shape: WorldShape,
): List<Convex> = when (shape) {
    is WorldShape.Circle -> createCircleWorldBorder(shape)
    is WorldShape.Rectangle -> createRectangleWorldBorder(shape)
    is WorldShape.Generic -> createGenericWorldBorder(shape)
    else -> throw IllegalArgumentException("Unsupported shape")
}

private fun createCircleWorldBorder(
    shape: WorldShape.Circle
): MutableList<out Convex> {
    val circle = Geometry.createPolygonalCircle(50, shape.radius)
    return Geometry.createLinks(circle.vertices.reversed(), true)
}

private fun createRectangleWorldBorder(
    shape: WorldShape.Rectangle
): MutableList<out Convex> {
    val rectangle = Geometry.createRectangle(shape.width, shape.height)
    return Geometry.createLinks(rectangle.vertices.reversed(), true)
}

private fun createGenericWorldBorder(
    shape: WorldShape.Generic
): MutableList<out Convex> {
    return Geometry.createLinks(shape.vertices.reversed(), true)
}