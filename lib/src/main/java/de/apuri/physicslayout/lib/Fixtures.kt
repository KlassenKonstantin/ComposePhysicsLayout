package de.apuri.physicslayout.lib

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Geometry

internal fun createFixtures(
    bodyMetaData: BodyMetaData,
    density: Density,
    scale: Double,
): List<Convex> = when {
    bodyMetaData.shape.isCircle() -> listOf(Geometry.createCircle(bodyMetaData.height / 2))
    bodyMetaData.shape.isRectangle() -> listOf(
        Geometry.createRectangle(
            bodyMetaData.width,
            bodyMetaData.height
        )
    )
    else -> createRoundedRectShape(bodyMetaData, density, scale)
}

private fun createRoundedRectShape(
    bodyMetaData: BodyMetaData,
    density: Density,
    scale: Double
): List<Convex> {
    val fixtures = mutableListOf<Convex>()
    val radius = bodyMetaData.shape.topStart.toPx(
        Size(bodyMetaData.width.toFloat(), bodyMetaData.height.toFloat()),
        density
    ).toDouble() / scale

    val halfBodyWidth = bodyMetaData.width / 2
    val halfBodyHeight = bodyMetaData.height / 2

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
        bodyMetaData.width - radius,
        bodyMetaData.height,
    )

    // Rect B
    fixtures += Geometry.createRectangle(
        bodyMetaData.width,
        bodyMetaData.height - radius,
    )

    return fixtures
}

private fun RoundedCornerShape.isCircle() = this == CircleShape
private fun RoundedCornerShape.isRectangle() =
    this == RoundedCornerShape(0.dp) ||
            this == RoundedCornerShape(0) ||
            this == RoundedCornerShape(0f)