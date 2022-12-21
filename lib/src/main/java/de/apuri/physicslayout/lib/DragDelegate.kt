package de.apuri.physicslayout.lib

import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.PinJoint
import org.dyn4j.world.World

internal interface DragDelegate {
    fun drag(
        bodyId: String,
        touchEvent: WorldTouchEvent,
        dragConfig: DragConfig.Draggable
    )
}

internal class DefaultDragDelegate(
    private val world: World<Body>
) : DragDelegate {
    private val joints = mutableMapOf<JointKey, PinJoint<Body>>()

    override fun drag(
        bodyId: String,
        touchEvent: WorldTouchEvent,
        dragConfig: DragConfig.Draggable
    ) {
        val key = JointKey(bodyId, touchEvent.pointerId)
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
    ) = world.findBodyById(jointKey.bodyId)?.let { body ->
        joints.getOrPut(jointKey) {
            PinJoint(
                body,
                body.getWorldPoint(touchEvent.localOffset),
                dragConfig.frequency,
                dragConfig.dampingRatio,
                dragConfig.maxForce
            ).also {
                world.addJoint(it)
            }
        }
    }
}

private data class JointKey(
    val bodyId: String,
    val pointerId: Long,
)