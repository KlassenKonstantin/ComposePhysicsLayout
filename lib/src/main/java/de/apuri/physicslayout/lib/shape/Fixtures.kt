package de.apuri.physicslayout.lib.shape

import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.Vector2
import org.dyn4j.geometry.hull.GiftWrap

internal fun createFixtures(
    shape: BodyShape,
): List<Convex> = when (shape) {
    is BodyShape.Circle -> listOf(Geometry.createCircle(shape.radius))
    is BodyShape.Rectangle -> listOf(
        Geometry.createRectangle(
            shape.width,
            shape.height
        )
    )
    is BodyShape.RoundedRectangle -> createRoundedRectShape(shape)
    is BodyShape.Generic -> createFromVertices(shape.vertices)
}

private fun createFromVertices(vertices: List<Vector2>): List<Convex> {
    return listOf(
        Geometry.createPolygon(*GiftWrap().generate(vertices).toTypedArray())
    )
}

private fun createRoundedRectShape(
    shape: BodyShape.RoundedRectangle
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
        shape.width - radius,
        shape.height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        shape.width,
        shape.height - radius,
    )

    return fixtures
}