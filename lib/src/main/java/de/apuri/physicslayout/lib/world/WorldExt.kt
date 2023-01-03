package de.apuri.physicslayout.lib.world

import de.apuri.physicslayout.lib.WorldBorder
import de.apuri.physicslayout.lib.body.Body
import org.dyn4j.geometry.Geometry
import org.dyn4j.geometry.MassType
import org.dyn4j.world.World

internal fun World<Body>.findBodyByUserData(userData: Any): Body? {
    bodyIterator.forEach {
        if (it.userData == userData) return it
    }

    return null
}

internal var World<Body>.metaData
    get() = userData as WorldMetaData
    set(value) { userData = value }

internal var World<Body>.width
    get() = metaData.width
    set(value) { metaData = metaData.copy(width = value) }

internal var World<Body>.height
    get() = metaData.height
    set(value) { metaData = metaData.copy(height = value) }

internal fun World<Body>.updateWorldSize(width: Double, height: Double) {
    this.width = width
    this.height = height
    updateWorldBorder()
}

private fun World<Body>.updateWorldBorder() {
    val borderThickness = 1.0
    val halfBorderThickness = borderThickness / 2

    val worldWidth = width
    val worldHeight = height
    val halfWidth = width / 2
    val halfHeight = height / 2

    // wall top
    findBodyByUserData(WorldBorder.TOP)?.apply {
        removeAllFixtures()
        addFixture(Geometry.createRectangle(worldWidth, borderThickness))
        setMass(MassType.INFINITE)
        translate(0.0, -halfHeight - halfBorderThickness)
    }

    // wall bottom
    findBodyByUserData(WorldBorder.BOTTOM)?.apply {
        removeAllFixtures()
        addFixture(Geometry.createRectangle(worldWidth, borderThickness))
        setMass(MassType.INFINITE)
        translate(0.0, halfHeight + halfBorderThickness)
    }

    // wall left
    findBodyByUserData(WorldBorder.LEFT)?.apply {
        removeAllFixtures()
        addFixture(Geometry.createRectangle(borderThickness, worldHeight))
        setMass(MassType.INFINITE)
        translate(-halfBorderThickness - halfWidth, 0.0)
    }

    // wall right
    findBodyByUserData(WorldBorder.RIGHT)?.apply {
        removeAllFixtures()
        addFixture(Geometry.createRectangle(borderThickness, worldHeight))
        setMass(MassType.INFINITE)
        translate(halfBorderThickness + halfWidth, 0.0)
    }
}