package de.apuri.physicslayout.lib.joint

import androidx.compose.ui.geometry.Offset
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.body.Body
import org.dyn4j.geometry.Vector2
import org.dyn4j.dynamics.joint.RevoluteJoint as WorldRevoluteJoint

data class RevoluteJoint(
    override val id: String? = null,
    val idA: String,
    val idB: String,
    val anchorARel: Offset = Offset.Zero
): Joint()

context(Simulation)
internal fun RevoluteJoint.toWorldRevoluteJoint(bodies: Map<String, Body>): WorldRevoluteJoint<Body>? {
    val bodyA = bodies[idA] ?: return null
    val bodyB = bodies[idB] ?: return null

    val localContactPointBodyA = Vector2(
        bodyA.width / 2 * anchorARel.x,
        bodyA.height / 2 * anchorARel.y,
    )

    return WorldRevoluteJoint(
        bodyA,
        bodyB,
        bodyA.getWorldPoint(localContactPointBodyA)
    ).apply {
        isCollisionAllowed = false
    }
}