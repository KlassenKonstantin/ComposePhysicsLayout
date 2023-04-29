package de.apuri.physicslayout.lib2.simulation

import de.apuri.physicslayout.lib2.simulation.SimulationEntity.Body
import org.dyn4j.world.World

internal class BodyManager(
    private val world: World<SimulationEntity<*>>,
) {
    val bodies: MutableMap<String, Body> = mutableMapOf()

    fun removeBody(id: String) {
        bodies[id]?.let {
            world.removeBody(it)
        }
        bodies.remove(id)
    }

    fun syncBody(id: String, body: SimulationBody) {
        bodies.getOrPut(id) {
            Body().apply {
                translate(body.initialOffset)
                world.addBody(this)
            }
        }.apply {
            updateFrom(body)
            userData = body
        }
    }
}