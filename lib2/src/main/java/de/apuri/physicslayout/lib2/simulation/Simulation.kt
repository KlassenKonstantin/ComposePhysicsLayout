package de.apuri.physicslayout.lib2.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import de.apuri.physicslayout.lib2.BodyConfig
import de.apuri.physicslayout.lib2.Clock
import de.apuri.physicslayout.lib2.drag.DefaultDragHandler
import de.apuri.physicslayout.lib2.drag.DragConfig
import de.apuri.physicslayout.lib2.drag.TouchType
import de.apuri.physicslayout.lib2.rememberClock
import org.dyn4j.geometry.Rotation
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

class Simulation internal constructor(
    private val world: World<SimulationEntity<*>>,
    private val clock: Clock,
) {

    internal val transformations = mutableStateMapOf<String, SimulationTransformation>()

    private val bodyManager = BodyManager(world)
    private val borderManager = BorderManager(world)
    private val dragHandler = DefaultDragHandler(world)

    fun setGravity(offset: Offset) {
        world.gravity = Vector2(offset.x.toDouble(), offset.y.toDouble())
    }

    suspend fun run() {
        clock.frames.collect { elapsed ->
            world.update(elapsed)
            updateTransformations()
        }
    }

    private fun updateTransformations() {
        bodyManager.bodies.mapValues {
            it.value.getTransformation()
        }.also {
            transformations.putAll(it)
        }
    }

    internal fun syncSimulationBorder(simulationBorder: SimulationBorder) {
        borderManager.syncBorder(simulationBorder)
    }

    internal fun syncSimulationBody(id: String, body: SimulationBody?) {
        if (body == null) {
            bodyManager.removeBody(id)
        } else {
            bodyManager.syncBody(id, body)
        }
    }

    internal fun drag(bodyId: String, touchEvent: SimulationTouchEvent, dragConfig: DragConfig) {
        bodyManager.bodies[bodyId]?.let {
            dragHandler.drag(it, touchEvent, dragConfig)
        }
    }

    fun resetBody(bodyId: String, pos: Vector2) {
        bodyManager.bodies[bodyId]?.let {
            it.translateToOrigin()
            it.translate(pos)
            it.transform.setRotation(Rotation.rotation0())
        }
    }
}

@Composable
fun rememberSimulation(clock: Clock = rememberClock()): Simulation {
    val simulation = remember(clock) {
        Simulation(createDefaultWorld(), clock)
    }

    LaunchedEffect(simulation) {
        simulation.run()
    }

    return simulation
}

private const val EARTH_GRAVITY = 9.81

private fun createDefaultWorld() = World<SimulationEntity<*>>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    settings.apply {
        isAtRestDetectionEnabled = false
        stepFrequency = 1.0 / 180
    }
}

internal data class SimulationBorder(
    val width: Double,
    val height: Double,
    val shape: SimulationShape?
)

internal data class SimulationBody(
    val width: Double,
    val height: Double,
    val shape: SimulationShape,
    val initialOffset: Vector2,
    val bodyConfig: BodyConfig,
)

internal data class SimulationTouchEvent(
    val pointerId: Long,
    val offset: Vector2,
    val type: TouchType
)

@Immutable
internal data class SimulationTransformation(
    val translationX: Double,
    val translationY: Double,
    val rotation: Double,
)