package de.apuri.physicslayout.lib2

import org.dyn4j.geometry.MassType
import org.dyn4j.world.World

//internal class BorderHolder(
//    world: World<WorldBody>,
//    private val scaleAware: ScaleAware
//) {
//    val worldBorder: WorldBody = WorldBody().apply {
//        setMassType(MassType.INFINITE)
//    }
//
//    init {
//        world.addBody(worldBorder)
//    }
//
//    fun setBorderFrom(entity: LayoutEntity) {
//        worldBorder.removeAllFixtures()
//
//        with(scaleAware) {
//            worldBorder.width = entity.width.toSimulationSize()
//            worldBorder.height = entity.height.toSimulationSize()
//
//            SimulationShape.fromPhysicsEntity(entity)?.let { shape ->
//                val fixtures = createWorldBorderFixtures(shape)
//                fixtures.forEach {
//                    worldBorder.addFixture(it)
//                    worldBorder.updateMass()
//                }
//            }
//        }
//    }
//}

internal class BorderHolder(
    private val world: World<Body>,
) {
    var currentBorder: SimulationBorder? = null

    val borderBody = Body().apply {
        setMassType(MassType.INFINITE)
        world.addBody(this)
    }

    fun syncBorder(newBorder: SimulationBorder) {
        if (currentBorder == newBorder) return

        borderBody.updateFrom(newBorder)
    }
}