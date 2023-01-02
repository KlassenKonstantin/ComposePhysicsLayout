package de.apuri.physicslayout.lib.shape

import de.apuri.physicslayout.lib.WorldBody
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry

internal fun createFixtures(
    worldBody: WorldBody,
): List<Convex> = when (worldBody.shape) {
    is BodyShape.Circle -> listOf(Geometry.createCircle(worldBody.height / 2))
    is BodyShape.Rectangle -> listOf(
        Geometry.createRectangle(
            worldBody.width,
            worldBody.height
        )
    )
    else -> createRoundedRectShape(worldBody)
}

private fun createRoundedRectShape(
    worldBody: WorldBody
): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val radius = (worldBody.shape as BodyShape.RoundedCornerRect).radius

    val halfBodyWidth = worldBody.width / 2
    val halfBodyHeight = worldBody.height / 2

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
        worldBody.width - radius,
        worldBody.height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        worldBody.width,
        worldBody.height - radius,
    )

    return fixtures
}