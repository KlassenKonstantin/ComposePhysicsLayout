package de.apuri.physicslayout.lib2

import org.dyn4j.geometry.MassType
import org.dyn4j.world.World

internal class BorderHolder(
    world: World<WorldBody>,
    private val scaleAware: ScaleAware
) {
    val worldBorder: WorldBody = WorldBody().apply {
        setMassType(MassType.INFINITE)
    }

    init {
        world.addBody(worldBorder)
    }

    fun setBorderFrom(physicsBorder: PhysicsBorder?) {
        worldBorder.removeAllFixtures()

        if (physicsBorder == null) return

        with(scaleAware) {
            worldBorder.width = physicsBorder.width.toWorldSize()
            worldBorder.height = physicsBorder.height.toWorldSize()

            val fixtures = createWorldBorderFixtures(physicsBorder.toWorldShape())
            fixtures.forEach {
                worldBorder.addFixture(it)
                worldBorder.updateMass()
            }
        }
    }
}