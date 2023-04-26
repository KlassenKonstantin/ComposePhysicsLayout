package de.apuri.physicslayout.lib2

import de.apuri.physicslayout.lib2.SimulationEntity.Body
import org.dyn4j.geometry.MassType

internal fun SimulationEntity.updateBorderFrom(border: SimulationBorder) {
    removeAllFixtures()
    border.shape?.toSimulationBorderFixtures()?.forEach {
        addFixture(it)
    }
    updateMass()
}

internal fun Body.updateFrom(body: SimulationBody) {
    angularDamping = body.bodyConfig.angularDamping.toDouble()
    setMass(if (body.bodyConfig.isStatic) MassType.INFINITE else MassType.NORMAL)

    val currentBody = (this.userData as? SimulationBody)

    if (currentBody?.shape != body.shape) {
        removeAllFixtures()
        body.shape.toSimulationBodyFixtures().forEach {
            addFixture(
                it,
                body.bodyConfig.density.toDouble(),
                body.bodyConfig.friction.toDouble(),
                body.bodyConfig.restitution.toDouble(),
            )
        }
    }

    if (currentBody?.bodyConfig != body.bodyConfig) {
        fixtures.forEach {
            it.density = body.bodyConfig.density.toDouble()
            it.friction = body.bodyConfig.friction.toDouble()
            it.restitution = body.bodyConfig.restitution.toDouble()
        }
    }
}