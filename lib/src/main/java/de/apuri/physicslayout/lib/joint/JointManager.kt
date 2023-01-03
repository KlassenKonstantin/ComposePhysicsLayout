package de.apuri.physicslayout.lib.joint

import de.apuri.physicslayout.lib.body.BodyManager
import org.dyn4j.dynamics.Body
import org.dyn4j.world.World

interface JointManager {
    fun addJoint(joint: Joint)
}

internal class DefaultJointManager(
    private val bodyManager: BodyManager,
    private val world: World<Body>
) : JointManager {
    override fun addJoint(joint: Joint) {

    }
}