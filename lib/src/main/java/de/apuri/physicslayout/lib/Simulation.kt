package de.apuri.physicslayout.lib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.body.ApplyBodySyncResult
import de.apuri.physicslayout.lib.body.Body
import de.apuri.physicslayout.lib.body.BodyManager
import de.apuri.physicslayout.lib.body.DefaultApplyBodySyncResult
import de.apuri.physicslayout.lib.body.LayoutBodySyncManager
import de.apuri.physicslayout.lib.body.createFixtures
import de.apuri.physicslayout.lib.body.toWorldShape
import de.apuri.physicslayout.lib.border.DefaultApplyNewWorldBorder
import de.apuri.physicslayout.lib.border.LayoutShape
import de.apuri.physicslayout.lib.border.toWorldShape
import de.apuri.physicslayout.lib.drag.DefaultDragDelegate
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.drag.DragDelegate
import de.apuri.physicslayout.lib.drag.TouchEvent
import de.apuri.physicslayout.lib.drag.toWorldTouchEvent
import de.apuri.physicslayout.lib.shape.SizeAwareShape
import de.apuri.physicslayout.lib.shape.WorldShape
import de.apuri.physicslayout.lib.shape.toWorldBodies
import kotlinx.coroutines.delay
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

class Simulation internal constructor(
    internal val scale: Double,
    private val world: World<Body>,
    internal val density: Density,
) {
    internal val transformations = mutableStateMapOf<String, LayoutTransformation>()

    private val dragDelegate: DragDelegate = DefaultDragDelegate(world)
    private val bodyManager: BodyManager = BodyManager(world)
    private val applyBodySyncResult: ApplyBodySyncResult = DefaultApplyBodySyncResult(bodyManager)
    private val applyNewWorldBorder: DefaultApplyNewWorldBorder = DefaultApplyNewWorldBorder(world)

    private var worldWidth = 0.0
    private var worldHeight = 0.0

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

            updateTransformations()

            delay(1)
        }
    }

    private fun updateTransformations() {
        bodyManager.bodies.mapValues {
            it.value.toLayoutTransformation()
        }.also {
            transformations.putAll(it)
        }
    }

    internal fun applyBodySyncResult(syncResult: LayoutBodySyncManager.SyncResult) {
        applyBodySyncResult(
            added = syncResult.added.toWorldBodies(),
            removed = syncResult.removed.toWorldBodies(),
            updated = syncResult.updated.toWorldBodies()
        )

        updateTransformations()
    }

    internal fun applyNewWorldBorder(layoutShape: LayoutShape) {
        applyNewWorldBorder(layoutShape.toWorldShape())
        worldWidth = layoutShape.width.toWorldSize()
        worldHeight = layoutShape.height.toWorldSize()
    }

    internal fun drag(bodyId: String, touchEvent: TouchEvent, dragConfig: DragConfig.Draggable) {
        bodyManager.bodies[bodyId]?.let {
            dragDelegate.drag(
                body = it,
                touchEvent = touchEvent.toWorldTouchEvent(),
                dragConfig = dragConfig
            )
        }
    }

    fun addBody(id: String, sizeAwareShape: SizeAwareShape, layoutCoordinates: LayoutCoordinates) {
        val worldShape = sizeAwareShape.toWorldShape()

        val bodyWidth = sizeAwareShape.width.toWorldSize()
        val bodyHeight = sizeAwareShape.height.toWorldSize()

        val halfWorldWidth = worldWidth / 2
        val halfWorldHeight = worldHeight / 2

        val lx = layoutCoordinates.positionInParent().x
        val lxe = layoutCoordinates.parentCoordinates?.size?.width ?: 1

        val ly = layoutCoordinates.positionInParent().y
        val lye = layoutCoordinates.parentCoordinates?.size?.height ?: 1

        val worldX = (lx * halfWorldWidth - lx * -halfWorldWidth + lxe * -halfWorldWidth) / lxe + bodyWidth / 2
        val worldY = (ly * halfWorldHeight - ly * -halfWorldHeight + lye * -halfWorldHeight) / lye + bodyHeight / 2

        val body = Body(
            bodyWidth,
            bodyHeight,
            worldX,
            worldY
        ).apply {
            angularDamping = 0.7
            isAtRestDetectionEnabled = false
            createFixtures(worldShape).forEach {
                addFixture(it, 1.0, 0.2, 0.4)
            }
            setMass(MassType.NORMAL)

            translate(worldX, worldY)
        }

        bodyManager.addBody(id, body)
    }
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

internal data class WorldBody(
    val id: String,
    val width: Double,
    val height: Double,
    val shape: WorldShape,
    val isStatic: Boolean,
    val initialTranslation: Vector2
)

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)

private val DEFAULT_SCALE = 64.dp

private const val EARTH_GRAVITY = 9.81

private val DEFAULT_WORLD get() = World<Body>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    settings.stepFrequency = 1.0 / 90
}