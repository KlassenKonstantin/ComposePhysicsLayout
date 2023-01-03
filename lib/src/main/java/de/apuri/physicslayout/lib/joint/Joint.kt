package de.apuri.physicslayout.lib.joint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.world.Body
import org.dyn4j.dynamics.joint.RevoluteJoint
import org.dyn4j.geometry.Vector2
import org.dyn4j.dynamics.joint.Joint as LibJoint

sealed class Joint {
    abstract val id: String?

    data class DistanceJoint(
        override val id: String? = null,
        val idA: String,
        val idB: String,
        val anchorARel: Offset? = null,
        val anchorBRel: Offset? = null,
        val lowerLimit: Dp? = null,
        val upperLimit: Dp? = null
    ): Joint()

    data class RevoluteJoint(
        override val id: String? = null,
        val idA: String,
        val idB: String,
        val anchorARel: Offset = Offset.Zero
    ): Joint()
}

context(Simulation)
fun Joint.toWorldJoint(
    bodies: Map<String, Body>
): LibJoint<Body>? {
    return when (this) {
        is Joint.DistanceJoint -> TODO()
        is Joint.RevoluteJoint -> toWorldRevoluteJoint(bodies)
        else -> null
    }
}

context(Simulation)
private fun Joint.RevoluteJoint.toWorldRevoluteJoint(bodies: Map<String, Body>): RevoluteJoint<Body>? {
    val bodyA = bodies[idA] ?: return null
    val bodyB = bodies[idB] ?: return null

    val localContactPointBodyA = Vector2(
        bodyA.width / 2 * anchorARel.x,
        bodyA.height / 2 * anchorARel.y,
    )

    return RevoluteJoint(
        bodyA,
        bodyB,
        bodyA.getWorldPoint(localContactPointBodyA)
    )
}