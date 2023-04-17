package de.apuri.physicslayout.lib2

import org.dyn4j.world.World

private const val ID_BORDER = "__border__"

internal class BodyHolder(
    private val world: World<WorldBody>,
) {
    val bodies: MutableMap<String, WorldBody> = mutableMapOf()

    fun addBody(id: String, worldBody: WorldBody) {
        world.addBody(worldBody)
        bodies[id]= worldBody
    }

    fun removeBody(id: String) {
        bodies[id]?.let {
            world.removeBody(it)
        }
        bodies.remove(id)
    }
}