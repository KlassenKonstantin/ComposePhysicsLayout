package de.apuri.physicslayout.lib.joint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.body.Body
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
}

context(Simulation)
fun Joint.toWorldJoint(
    bodies: Map<String, Body>
): LibJoint<Body>? {
    return when (this) {
        is Joint.DistanceJoint -> TODO()
        is RevoluteJoint -> toWorldRevoluteJoint(bodies)
        else -> null
    }
}