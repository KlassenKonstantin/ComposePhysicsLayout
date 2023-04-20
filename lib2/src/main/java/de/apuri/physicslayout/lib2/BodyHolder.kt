package de.apuri.physicslayout.lib2

import org.dyn4j.world.World

private const val ID_BORDER = "__border__"

internal class BodyHolder(
    private val world: World<Body>,
) {
    val bodies: MutableMap<String, Body> = mutableMapOf()

    fun addBody(id: String, body: Body) {
        world.addBody(body)
        bodies[id]= body
    }

    fun removeBody(id: String) {
        bodies[id]?.let {
            world.removeBody(it)
        }
        bodies.remove(id)
    }
}