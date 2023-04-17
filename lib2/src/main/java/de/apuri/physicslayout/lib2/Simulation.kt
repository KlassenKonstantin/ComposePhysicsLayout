package de.apuri.physicslayout.lib2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World

internal interface ScaleAware {
    val scale: Double
    val density: Density
}

class Simulation internal constructor(
    override val scale: Double,
    override val density: Density,
    private val world: World<WorldBody>,
) : ScaleAware {

    internal val transformations = mutableStateMapOf<String, LayoutTransformation>()

    private val bodyHolder = BodyHolder(world)
    private val borderHolder = BorderHolder(world, this)

    private var borderCoordinates: LayoutCoordinates? = null

    fun setGravity(offset: Offset) {
        world.gravity = offset.toVector2()
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
            it.value.toLayoutTransformation()
        }.also {
            transformations.putAll(it)
        }
    }

    fun syncPhysicsBody(id: String, physicsBody: PhysicsBody?) {
        if (physicsBody == null) {
            bodyHolder.removeBody(id)
            return
        }

        val worldShape = physicsBody.toWorldShape()

        val bodyWidth = physicsBody.width.toWorldSize()
        val bodyHeight = physicsBody.height.toWorldSize()

        val worldWidth = borderHolder.worldBorder.width
        val worldHeight = borderHolder.worldBorder.height

        val halfWorldWidth = worldWidth / 2
        val halfWorldHeight = worldHeight / 2

        val lx = borderCoordinates!!.localPositionOf(physicsBody.layoutCoordinates.parentCoordinates!!, physicsBody.layoutCoordinates.positionInParent()).x
        val lxe = worldWidth.toLayoutSize()

        val ly = borderCoordinates!!.localPositionOf(physicsBody.layoutCoordinates.parentCoordinates!!, physicsBody.layoutCoordinates.positionInParent()).y
        val lye = worldHeight.toLayoutSize()

        val worldX = (lx * halfWorldWidth - lx * -halfWorldWidth + lxe * -halfWorldWidth) / lxe + bodyWidth / 2
        val worldY = (ly * halfWorldHeight - ly * -halfWorldHeight + lye * -halfWorldHeight) / lye + bodyHeight / 2

        WorldBody(
            width = bodyWidth,
            height = bodyHeight,
            offset = Vector2(worldX, worldY)
        ).apply {
            angularDamping = 0.7
            isAtRestDetectionEnabled = false
            createFixtures(worldShape).forEach {
                addFixture(it, 1.0, 0.2, 0.4)
            }
            setMass(MassType.NORMAL)

            translate(worldX, worldY)
        }.also {
            bodyHolder.addBody(id, it)
        }
    }

    fun syncPhysicsBorder(physicsBorder: PhysicsBorder?) {
        borderHolder.setBorderFrom(physicsBorder)
        borderCoordinates = physicsBorder?.layoutCoordinates
    }
}

@Composable
fun rememberSimulation(
    scale: Dp = DEFAULT_SCALE,
): Simulation {
    val scalePx = LocalDensity.current.run { scale.toPx().toDouble() }
    val density = LocalDensity.current

    val simulation = remember(scale) {
        Simulation(scalePx, density, createDefaultWorld())
    }

    LaunchedEffect(simulation) {
        simulation.run()
    }

    return simulation
}

private val DEFAULT_SCALE = 64.dp

private const val EARTH_GRAVITY = 9.81

private fun createDefaultWorld() = World<WorldBody>().apply {
    gravity = Vector2(0.0, EARTH_GRAVITY)
    settings.stepFrequency = 1.0 / 90
}

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)

interface PhysicsEntity {
    val width: Int
    val height: Int
    val shape: Shape
}
data class PhysicsBody(
    override val width: Int,
    override val height: Int,
    override val shape: Shape,
    val layoutCoordinates: LayoutCoordinates,
) : PhysicsEntity

data class PhysicsBorder(
    override val width: Int,
    override val height: Int,
    override val shape: Shape,
    val layoutCoordinates: LayoutCoordinates,
) : PhysicsEntity