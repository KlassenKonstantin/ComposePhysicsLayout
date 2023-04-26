package de.apuri.physicslayout.lib2

import android.util.Log
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry

internal fun SimulationShape.toSimulationBorderFixtures(): List<Convex> = when (this) {
    is SimulationShape.Circle -> toCircleWorldBorder()
    is SimulationShape.Rectangle -> toRectangleWorldBorder()
    is SimulationShape.Generic -> toGenericWorldBorder()
    else -> throw IllegalArgumentException("Unsupported shape")
}

private fun SimulationShape.Circle.toCircleWorldBorder(): MutableList<out Convex> {
    val circle = Geometry.createPolygonalCircle(50, radius)
    return Geometry.createLinks(circle.vertices.reversed(), true)
}

private fun SimulationShape.Rectangle.toRectangleWorldBorder(): MutableList<out Convex> {
    val rectangle = Geometry.createRectangle(width, height)
    return Geometry.createLinks(rectangle.vertices.reversed(), true)
}

private fun SimulationShape.Generic.toGenericWorldBorder(): MutableList<out Convex> {
    return Geometry.createLinks(vertices.reversed(), true)
}