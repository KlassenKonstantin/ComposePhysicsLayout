package de.apuri.physicslayout.lib

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

class Simulation internal constructor(
    private val scale: Double,
    private val world: World<Body>,
    private val density: Density,
) {
    internal val transformations = mutableStateMapOf<String, Transformation>()
    private val dragDelegate: DragDelegate = DefaultDragDelegate(world)

    fun setGravity(offset: Offset) {
        world.gravity = offset.toVector2()
    }

    internal suspend fun run() {
        var last = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val elapsed = (now - last).toDouble() / 1.0e9
            last = now

            world.update(elapsed)

            world.bodies.filter {
                it.userData is BodyMetaData
            }.associate {
                (it.userData as BodyMetaData).id to it.toTransformation()
            }.also {
                transformations.putAll(it)
            }

            delay(1)
        }
    }

    internal fun updateWorldSize(intSize: IntSize) {
        world.updateWorldSize(
            intSize.width / scale,
            intSize.height / scale,
        )
    }

    internal fun syncBodies(layoutItems: List<LayoutItem>) {
        removeBodiesWithIdNotIn(layoutItems.map { it.id })
        layoutItems.forEach {
            val bodyMetaData = it.toBodyMetaData()
            val body = world.findBodyById(bodyMetaData.id)
            if (body == null) {
                createBody(bodyMetaData, it).also { newBody ->
                    world.addBody(newBody)
                    transformations[bodyMetaData.id] = newBody.toTransformation()
                }
            } else {
                updateBody(body, bodyMetaData)
            }
        }
    }

    internal fun drag(bodyId: String, touchEvent: TouchEvent, dragConfig: DragConfig.Draggable) {
        dragDelegate.drag(bodyId, touchEvent.toWorldTouchEvent(), dragConfig)
    }

    private fun createBody(
        bodyMetaData: BodyMetaData,
        layoutItem: LayoutItem,
    ) = Body().apply {
        angularDamping = 0.7
        isAtRestDetectionEnabled = false
        userData = bodyMetaData
        applyFixtures(bodyMetaData)
        setMass(if (layoutItem.isStatic) MassType.INFINITE else MassType.NORMAL)
        translate(
            Vector2(
                layoutItem.initialTranslation.x / scale,
                layoutItem.initialTranslation.y / scale
            )
        )
        applyImpulse(layoutItem.initialImpulse.toVector2())
    }

    private fun updateBody(
        body: Body,
        newBodyMetaData: BodyMetaData
    ) = body.apply {
        val currentMetaData = userData as BodyMetaData
        if (currentMetaData == newBodyMetaData) return@apply

        userData = newBodyMetaData
        removeAllFixtures()
        applyFixtures(newBodyMetaData)
        setMass(if (newBodyMetaData.isStatic) MassType.INFINITE else MassType.NORMAL)
        updateMass()
    }

    private fun Body.applyFixtures(bodyMetaData: BodyMetaData) {
        createFixtures(bodyMetaData, density, scale).forEach {
            addFixture(it, 1.0, 0.2, 0.4)
        }
    }

    private fun Body.toTransformation() = Transformation(
        translationX = (transform.translationX * scale).toFloat(),
        translationY = (transform.translationY * scale).toFloat(),
        rotation = transform.rotation.toDegrees().toFloat(),
    )

    private fun removeBodiesWithIdNotIn(currentIds: List<String>) {
        val removedBodyIds = world.removeBodiesWithIdNotIn(currentIds)
        removedBodyIds.forEach {
            transformations.remove(it)
        }
    }

    private fun LayoutItem.toBodyMetaData() = BodyMetaData(
        id = id,
        width = width / scale,
        height = height / scale,
        shape = shape,
        isStatic = isStatic
    )

    private fun Offset.toVector2() = Vector2(x.toDouble(), y.toDouble())
    private fun Offset.toWorldVector2() = Vector2(x.toDouble(), y.toDouble()).divide(scale)
    private fun TouchEvent.toWorldTouchEvent() = WorldTouchEvent(
        pointerId = pointerId,
        localOffset = localOffset.toWorldVector2(),
        type = type
    )
}

@Composable
fun rememberSimulation(
    scale: Dp = DEFAULT_SCALE,
): Simulation {
    val scalePx = LocalDensity.current.run { scale.toPx().toDouble() }
    val density = LocalDensity.current

    val simulation = remember(scale) {
        Simulation(scalePx, DEFAULT_WORLD, density)
    }

    LaunchedEffect(simulation) {
        simulation.run()
    }

    return simulation
}

data class WorldMetaData(
    val width: Double = 0.0,
    val height: Double = 0.0,
)

data class BodyMetaData(
    val id: String,
    val width: Double,
    val height: Double,
    val shape: RoundedCornerShape,
    val isStatic: Boolean
)

data class LayoutItem(
    val id: String,
    val width: Int,
    val height: Int,
    val shape: RoundedCornerShape,
    val isStatic: Boolean,
    val initialTranslation: Offset,
    val initialImpulse: Offset,
)

internal enum class WorldBorder {
    TOP, BOTTOM, LEFT, RIGHT
}

data class WorldTouchEvent(
    val pointerId: Long,
    val localOffset: Vector2,
    val type: TouchType
)

@Immutable
internal data class Transformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)

private val DEFAULT_SCALE = 64.dp

private const val EARTH_GRAVITY = 9.81

private val DEFAULT_WORLD = World<Body>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    userData = WorldMetaData()
    settings.stepFrequency = 1.0 / 90
    addBody(Body().apply { userData = WorldBorder.TOP })
    addBody(Body().apply { userData = WorldBorder.BOTTOM })
    addBody(Body().apply { userData = WorldBorder.LEFT })
    addBody(Body().apply { userData = WorldBorder.RIGHT })
}