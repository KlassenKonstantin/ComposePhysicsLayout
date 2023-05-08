package de.apuri.physicslayout.lib2.drag

import androidx.compose.runtime.Immutable

@Immutable
/**
 * Connects the body and the touch point with a [org.dyn4j.dynamics.joint.PinJoint].
 * Each pointer creates its own PinJoint. [frequency] defines the oscillation frequency in hz.
 * [dampingRatio] defines the damping ratio. [maxForce] defines the maximum force.
 */
data class DragConfig(
    val frequency: Double = DEF_FREQUENCY,
    val dampingRatio: Double = DEF_DAMPING_RATIO,
    val maxForce: Double = DEF_MAX_FORCE,
)

const val DEF_FREQUENCY = 15.0
const val DEF_DAMPING_RATIO = 0.3
const val DEF_MAX_FORCE = 10_000.0