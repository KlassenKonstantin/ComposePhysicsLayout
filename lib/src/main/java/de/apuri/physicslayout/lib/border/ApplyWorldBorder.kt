package de.apuri.physicslayout.lib.border

import de.apuri.physicslayout.lib.body.Body
import de.apuri.physicslayout.lib.shape.WorldShape
import org.dyn4j.geometry.MassType
import org.dyn4j.world.World

internal fun interface ApplyNewWorldBorder {
    operator fun invoke(shape: WorldShape?)
}

internal class DefaultApplyNewWorldBorder(
    world: World<Body>
) : ApplyNewWorldBorder {
    private var worldBorder: Body = Body().apply {
        setMassType(MassType.INFINITE)
    }

    init {
        world.addBody(worldBorder)
    }

    override fun invoke(shape: WorldShape?) {
        worldBorder.removeAllFixtures()

        if (shape != null) {
            val fixtures = createWorldBorderFixtures(shape)
            fixtures.forEach {
                worldBorder.addFixture(it)
                worldBorder.updateMass()
            }
        }
    }
}