package de.apuri.physicslayout.lib

import de.apuri.physicslayout.lib.world.Body
import org.dyn4j.world.World

internal class BodyManager(
    private val world: World<Body>
) {
    internal val bodies: MutableMap<String, Body> = mutableMapOf()

    internal fun addBody(id: String, body: Body) {
        world.addBody(body)
        bodies[id]= body
    }

    internal fun removeBody(id: String) {
        bodies[id]?.let {
            world.removeBody(it)
        }
        bodies.remove(id)
    }
}