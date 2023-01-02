package de.apuri.physicslayout.lib.joint

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

sealed class Joint {
    data class DistanceJoint(
        val idA: String,
        val idB: String,
        val anchorARel: Offset? = null,
        val anchorBRel: Offset? = null,
        val lowerLimit: Dp? = null,
        val upperLimit: Dp? = null
    ): Joint()
}