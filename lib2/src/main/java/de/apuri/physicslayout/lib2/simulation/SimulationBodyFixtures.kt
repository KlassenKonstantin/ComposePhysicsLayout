package de.apuri.physicslayout.lib2.simulation

import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.Vector2
import org.dyn4j.geometry.hull.GiftWrap
import kotlin.math.sqrt

internal fun SimulationShape.toSimulationBodyFixtures(): List<Convex> = when (this) {
    is SimulationShape.Circle -> listOf(Geometry.createCircle(radius))
    is SimulationShape.Rectangle -> listOf(Geometry.createRectangle(width, height))
    is SimulationShape.RoundedCornerRectangle -> toRoundedRectShape()
    is SimulationShape.CutCornerRectangle -> toCutCornerRectShape()
    is SimulationShape.Generic -> createFromVertices(vertices)
}

private fun createFromVertices(vertices: List<Vector2>): List<Convex> {
    return listOf(
        Geometry.createPolygon(*GiftWrap().generate(vertices).toTypedArray())
    )
}

private fun SimulationShape.RoundedCornerRectangle.toRoundedRectShape(): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val radius = cornerRadius

    val halfBodyWidth = width / 2
    val halfBodyHeight = height / 2

    // Top left
    fixtures += Geometry.createCircle(radius).apply {
        translate(-halfBodyWidth + radius, -halfBodyHeight + radius)
    }

    // Top right
    fixtures += Geometry.createCircle(radius).apply {
        translate(halfBodyWidth - radius, -halfBodyHeight + radius)
    }

    // Bottom left
    fixtures += Geometry.createCircle(radius).apply {
        translate(-halfBodyWidth + radius, halfBodyHeight - radius)
    }

    // Bottom right
    fixtures += Geometry.createCircle(radius).apply {
        translate(halfBodyWidth - radius, halfBodyHeight - radius)
    }

    // Rect A
    fixtures += Geometry.createRectangle(
        width - 2 * radius,
        height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        width,
        height - 2 * radius,
    )

    return fixtures
}

private fun SimulationShape.CutCornerRectangle.toCutCornerRectShape(): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val cutLength = cutLength
    val legLength = (sqrt(2.0 / 2)) * cutLength

    val halfBodyWidth = width / 2
    val halfBodyHeight = height / 2

    // Top left
    fixtures += Geometry.createTriangle(Vector2(), Vector2(-legLength, 0.0), Vector2(0.0, -legLength)).apply {
        translate(-halfBodyWidth + legLength, -halfBodyHeight + legLength)
    }

    // Top right
    fixtures += Geometry.createTriangle(Vector2(), Vector2(0.0, -legLength), Vector2(legLength, 0.0)).apply {
        translate(halfBodyWidth - legLength, -halfBodyHeight + legLength)
    }

    // Bottom left
    fixtures += Geometry.createTriangle(Vector2(), Vector2(0.0, legLength), Vector2(-legLength, 0.0)).apply {
        translate(-halfBodyWidth + legLength, halfBodyHeight - legLength)
    }

    // Bottom right
    fixtures += Geometry.createTriangle(Vector2(), Vector2(legLength, 0.0), Vector2(0.0, legLength)).apply {
        translate(halfBodyWidth - legLength, halfBodyHeight - legLength)
    }

    // Rect A
    fixtures += Geometry.createRectangle(
        width - 2 * legLength,
        height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        width,
        height - 2 * legLength,
    )

    return fixtures
}