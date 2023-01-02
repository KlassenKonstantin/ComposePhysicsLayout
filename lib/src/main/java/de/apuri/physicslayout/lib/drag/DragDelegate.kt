package de.apuri.physicslayout.lib.drag

import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.PinJoint
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

internal interface DragDelegate {
    fun drag(
        body: Body,
        touchEvent: WorldTouchEvent,
        dragConfig: DragConfig.Draggable
    )
}

internal class DefaultDragDelegate(
    private val world: World<Body>
) : DragDelegate {
    private val joints = mutableMapOf<JointKey, PinJoint<Body>>()

    override fun drag(
        body: Body,
        touchEvent: WorldTouchEvent,
        dragConfig: DragConfig.Draggable
    ) {
        val key = JointKey(body, touchEvent.pointerId)
        when (touchEvent.type) {
            TouchType.DOWN -> {
                getOrPutJoint(key, touchEvent, dragConfig)
            }

            TouchType.MOVE -> {
                getOrPutJoint(key, touchEvent, dragConfig)?.apply {
                    target = body1.getWorldPoint(touchEvent.localOffset)
                    frequency = dragConfig.frequency
                    dampingRatio = dragConfig.dampingRatio
                    maximumForce = dragConfig.maxForce
                }

            }

            TouchType.UP -> {
                world.removeJoint(joints.remove(key))
            }
        }
    }

    private fun getOrPutJoint(
        jointKey: JointKey,
        touchEvent: WorldTouchEvent,
        dragConfig: DragConfig.Draggable
    ) = joints.getOrPut(jointKey) {
        PinJoint(
            jointKey.body,
            jointKey.body.getWorldPoint(touchEvent.localOffset),
            dragConfig.frequency,
            dragConfig.dampingRatio,
            dragConfig.maxForce
        ).also {
            world.addJoint(it)
        }
    }
}

internal data class WorldTouchEvent(
    val pointerId: Long,
    val localOffset: Vector2,
    val type: TouchType
)

private data class JointKey(
    val body: Body,
    val pointerId: Long,
)