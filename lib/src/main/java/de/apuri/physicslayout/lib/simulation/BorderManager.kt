package de.apuri.physicslayout.lib.simulation

import org.dyn4j.geometry.MassType
import org.dyn4j.world.World

internal class BorderManager(
    private val world: World<SimulationEntity<*>>,
) {
    private var currentBorder: SimulationBorder? = null

    private val borderSimulationEntity = SimulationEntity.Border().apply {
        setMassType(MassType.INFINITE)
        world.addBody(this)
    }

    fun syncBorder(newBorder: SimulationBorder) {
        if (currentBorder == newBorder) return

        borderSimulationEntity.updateFrom(newBorder)
    }
}