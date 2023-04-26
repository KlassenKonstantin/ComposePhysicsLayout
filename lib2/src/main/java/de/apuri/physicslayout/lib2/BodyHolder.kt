package de.apuri.physicslayout.lib2

import de.apuri.physicslayout.lib2.SimulationEntity.Body
import org.dyn4j.world.World

internal class BodyHolder(
    private val world: World<SimulationEntity>,
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
                isAtRestDetectionEnabled = false
                translate(body.initialOffset)
                world.addBody(this)
            }
        }.apply {
            updateFrom(body)
            userData = body
        }
    }
}