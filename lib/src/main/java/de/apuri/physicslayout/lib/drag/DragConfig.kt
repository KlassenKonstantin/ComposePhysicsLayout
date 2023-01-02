package de.apuri.physicslayout.lib.drag

import androidx.compose.runtime.Immutable

@Immutable
sealed class DragConfig {
    object NotDraggable: DragConfig()

    /**
     * Connects the body and the touch point with a [org.dyn4j.dynamics.joint.PinJoint].
     * Each pointer creates its own PinJoint.
     */
    data class Draggable(
        /**
         * The oscillation frequency in hz
         */
        val frequency: Double = DEF_FREQUENCY,

        /**
         * The damping ratio
         */
        val dampingRatio: Double = DEF_DAMPING_RATIO,

        /**
         * The maximum force
         */
        val maxForce: Double = DEF_MAX_FORCE,
    ) : DragConfig()
}

const val DEF_FREQUENCY = 15.0
const val DEF_DAMPING_RATIO = 0.3
const val DEF_MAX_FORCE = 700.0