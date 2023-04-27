package de.apuri.physicslayout.lib2.drag

import de.apuri.physicslayout.lib2.simulation.SimulationEntity
import de.apuri.physicslayout.lib2.simulation.SimulationTouchEvent
import org.dyn4j.dynamics.joint.Joint
import org.dyn4j.dynamics.joint.PinJoint
import org.dyn4j.world.World

internal interface DragHandler {
    fun drag(
        body: SimulationEntity.Body,
        touchEvent: SimulationTouchEvent,
        dragConfig: DragConfig
    )
}

internal class DefaultDragHandler(
    private val world: World<SimulationEntity<*>>
) : DragHandler {
    private val joints = mutableMapOf<JointKey, PinJoint<SimulationEntity.Body>>()

    override fun drag(
        body: SimulationEntity.Body,
        touchEvent: SimulationTouchEvent,
        dragConfig: DragConfig
    ) {
        val key = JointKey(body, touchEvent.pointerId)
        when (touchEvent.type) {
            TouchType.DOWN -> {
                getOrPutJoint(key, touchEvent, dragConfig)
            }

            TouchType.MOVE -> {
                getOrPutJoint(key, touchEvent, dragConfig).apply {
                    target = body.getWorldPoint(touchEvent.offset)
                    springFrequency = dragConfig.frequency
                    springDampingRatio = dragConfig.dampingRatio
                    maximumSpringForce = dragConfig.maxForce
                }

            }

            TouchType.UP -> {
                world.removeJoint(joints.remove(key) as Joint<SimulationEntity<*>>)
            }
        }
    }

    private fun getOrPutJoint(
        jointKey: JointKey,
        touchEvent: SimulationTouchEvent,
        dragConfig: DragConfig
    ) = joints.getOrPut(jointKey) {
        PinJoint(
            jointKey.body,
            jointKey.body.getWorldPoint(touchEvent.offset),
        ).apply {
            isSpringEnabled = true
            springFrequency = dragConfig.frequency
            springDampingRatio = dragConfig.dampingRatio
            maximumSpringForce = dragConfig.maxForce
        }.also {
            world.addJoint(it as Joint<SimulationEntity<*>>)
        }
    }
}

private data class JointKey(
    val body: SimulationEntity.Body,
    val pointerId: Long,
)