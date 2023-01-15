package de.apuri.physicslayout.lib.body

import de.apuri.physicslayout.lib.shape.WorldShape
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.Vector2
import org.dyn4j.geometry.hull.GiftWrap
import kotlin.math.sqrt

internal fun createFixtures(
    shape: WorldShape,
): List<Convex> = when (shape) {
    is WorldShape.Circle -> listOf(Geometry.createCircle(shape.radius))
    is WorldShape.Rectangle -> listOf(
        Geometry.createRectangle(
            shape.width,
            shape.height
        )
    )
    is WorldShape.RoundedCornerRectangle -> createRoundedRectShape(shape)
    is WorldShape.CutCornerRectangle -> createCutCornerRectShape(shape)
    is WorldShape.Generic -> createFromVertices(shape.vertices)
}

private fun createFromVertices(vertices: List<Vector2>): List<Convex> {
    return listOf(
        Geometry.createPolygon(*GiftWrap().generate(vertices).toTypedArray())
    )
}

private fun createRoundedRectShape(
    shape: WorldShape.RoundedCornerRectangle
): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val radius = shape.cornerRadius

    val halfBodyWidth = shape.width / 2
    val halfBodyHeight = shape.height / 2

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
        shape.width - 2 * radius,
        shape.height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        shape.width,
        shape.height - 2 * radius,
    )

    return fixtures
}

private fun createCutCornerRectShape(
    shape: WorldShape.CutCornerRectangle
): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val cutLength = shape.cutLength
    val legLength = (sqrt(2.0 / 2)) * cutLength

    val halfBodyWidth = shape.width / 2
    val halfBodyHeight = shape.height / 2

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
        shape.width - 2 * legLength,
        shape.height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        shape.width,
        shape.height - 2 * legLength,
    )

    return fixtures
}