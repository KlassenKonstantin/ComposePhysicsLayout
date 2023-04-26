package de.apuri.physicslayout.lib2.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import de.apuri.physicslayout.lib2.BodyConfig
import kotlinx.coroutines.delay
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

class Simulation internal constructor(
    private val world: World<SimulationEntity<*>>,
) {

    internal val transformations = mutableStateMapOf<String, SimulationTransformation>()

    private val bodyHolder = BodyHolder(world)
    private val borderHolder = BorderHolder(world)

    fun setGravity(offset: Offset) {
        world.gravity = Vector2(offset.x.toDouble(), offset.y.toDouble())
    }

    suspend fun run() {
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
        bodyHolder.bodies.mapValues {
            it.value.getTransformation()
        }.also {
            transformations.putAll(it)
        }
    }

    internal fun syncSimulationBorder(simulationBorder: SimulationBorder) {
        borderHolder.syncBorder(simulationBorder)
    }

    internal fun syncSimulationBody(id: String, body: SimulationBody?) {
        if (body == null) {
            bodyHolder.removeBody(id)
        } else {
            bodyHolder.syncBody(id, body)
        }
    }
}

@Composable
fun rememberSimulation(): Simulation {
    val simulation = remember {
        // Add clock as parameter
        Simulation(createDefaultWorld())
    }

    LaunchedEffect(simulation) {
        simulation.run()
    }

    return simulation
}

private const val EARTH_GRAVITY = 9.81

private fun createDefaultWorld() = World<SimulationEntity<*>>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    settings.stepFrequency = 1.0 / 90
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

@Immutable
internal data class SimulationTransformation(
    val translationX: Double,
    val translationY: Double,
    val rotation: Double,
)