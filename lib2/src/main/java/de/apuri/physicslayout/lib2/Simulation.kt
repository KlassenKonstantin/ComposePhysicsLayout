package de.apuri.physicslayout.lib2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import kotlinx.coroutines.delay
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

//internal interface ScaleAware {
//    val scale: Double
//    val density: Density
//}

class Simulation internal constructor(
    private val world: World<Body>,
) {

    internal val transformations = mutableStateMapOf<String, LayoutTransformation>()

    private val bodyHolder = BodyHolder(world)
    private val borderHolder = BorderHolder(world)

    //lateinit var physicsLayoutCoordinates: LayoutCoordinates

    fun setGravity(offset: Offset) {
//        world.gravity = offset.toVector2()
    }

    suspend fun run() {
        var last = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val elapsed = (now - last).toDouble() / 1.0e9
            last = now

            world.update(elapsed)

//            updateTransformations()

            delay(1)
        }
    }

//    private fun updateTransformations() {
//        bodyHolder.bodies.mapValues {
//            it.value.toLayoutTransformation()
//        }.also {
//            transformations.putAll(it)
//        }
//    }

//    fun syncPhysicsBody(id: String, entity: LayoutEntity?) {
//        if (entity == null) {
//            bodyHolder.removeBody(id)
//            return
//        }
//
//        val simulationShape = SimulationShape.fromPhysicsEntity(entity)
//
//        val bodyWidth = entity.width.toSimulationSize()
//        val bodyHeight = entity.height.toSimulationSize()
//
//        val worldWidth = borderHolder.worldBorder.width
//        val worldHeight = borderHolder.worldBorder.height
//
//        val halfWorldWidth = worldWidth / 2
//        val halfWorldHeight = worldHeight / 2
//
//        val lx = physicsLayoutCoordinates.localPositionOf(entity.layoutCoordinates.parentCoordinates!!, entity.layoutCoordinates.positionInParent()).x
//        val lxe = worldWidth.toLayoutSize()
//
//        val ly = physicsLayoutCoordinates.localPositionOf(entity.layoutCoordinates.parentCoordinates!!, entity.layoutCoordinates.positionInParent()).y
//        val lye = worldHeight.toLayoutSize()
//
//        val worldX = (lx * halfWorldWidth - lx * -halfWorldWidth + lxe * -halfWorldWidth) / lxe + bodyWidth / 2
//        val worldY = (ly * halfWorldHeight - ly * -halfWorldHeight + lye * -halfWorldHeight) / lye + bodyHeight / 2
//
//        WorldBody(
//            width = bodyWidth,
//            height = bodyHeight,
//            offset = Vector2(worldX, worldY)
//        ).apply {
//            angularDamping = 0.7
//            isAtRestDetectionEnabled = false
//            if (simulationShape != null) {
//                createFixtures(simulationShape).forEach {
//                    addFixture(it, 1.0, 0.2, 0.4)
//                }
//            }
//            setMass(MassType.NORMAL)
//
//            translate(worldX, worldY)
//        }.also {
//            bodyHolder.addBody(id, it)
//        }
//    }

    fun syncPhysicsBorder(entity: LayoutEntity) {
//        borderHolder.setBorderFrom(entity)
//        physicsLayoutCoordinates = entity.layoutCoordinates
    }

    internal fun syncSimulationBorder(simulationBorder: SimulationBorder) {
        borderHolder.syncBorder(simulationBorder)
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

private fun createDefaultWorld() = World<Body>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    settings.stepFrequency = 1.0 / 90
}

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)

interface LayoutEntity {
    val width: Int
    val height: Int
    val layoutCoordinates: LayoutCoordinates
}

data class LayoutBody(
    override val width: Int,
    override val height: Int,
    override val layoutCoordinates: LayoutCoordinates,
    val shape: Shape
) : LayoutEntity

data class LayoutBorder(
    override val width: Int,
    override val height: Int,
    override val layoutCoordinates: LayoutCoordinates,
    val shape: Shape?
) : LayoutEntity

// Das hier:
internal data class SimulationBorder(
    val width: Double,
    val height: Double,
    val shape: SimulationShape?
)