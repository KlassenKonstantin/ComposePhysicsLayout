package de.apuri.physicslayout.lib

import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.PinJoint
import org.dyn4j.world.World

interface DragDelegate {
    fun drag(bodyId: String, touchEvent: WorldTouchEvent)
}

internal class DefaultDragDelegate(
    private val world: World<Body>
) : DragDelegate {
    private val joints = mutableMapOf<JointKey, PinJoint<Body>>()
    override fun drag(bodyId: String, touchEvent: WorldTouchEvent) {
        when (touchEvent.type) {
            TouchType.DOWN -> {
                world.findBodiesByUserDataType<BodyMetaData>().firstOrNull {
                    (it.userData as BodyMetaData).id == bodyId
                }?.let {
                    val joint = PinJoint(it, it.getWorldPoint(touchEvent.localOffset), 10.0, 1.0, 700.0)
                    joints[JointKey(bodyId, touchEvent.pointerId)] = joint
                    world.addJoint(joint)
                }
            }

            TouchType.MOVE -> {
                world.findBodiesByUserDataType<BodyMetaData>().firstOrNull {
                    (it.userData as BodyMetaData).id == bodyId
                }?.let {
                    joints[JointKey(bodyId, touchEvent.pointerId)]?.target = it.getWorldPoint(touchEvent.localOffset)
                }
            }

            TouchType.UP -> {
                world.removeJoint(joints.remove(JointKey(bodyId, touchEvent.pointerId)))
            }
        }
    }
}

private data class JointKey(
    val bodyId: String,
    val pointerId: Long,
)