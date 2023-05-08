package de.apuri.physicslayout.lib

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import de.apuri.physicslayout.lib.conversion.LocalLayoutToSimulation
import de.apuri.physicslayout.lib.conversion.LocalSimulationToLayout
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.drag.touch
import java.util.UUID

/**
 * Introduces the Composable this Modifier is applied to to the physics world.
 *
 * This must be used on a Composable that is a direct or indirect child of a PhysicsLayout or else an Exception is
 * thrown.
 *
 * [shape] defines the shape of this body. This should be the same as the shape of the Composable this is applied to in
 * most cases. If [dragConfig] is not `null`, the body can be dragged by the user and behaves as defined in [DragConfig].
 */
fun Modifier.physicsBody(
    id: String? = null,
    shape: Shape = RectangleShape,
    bodyConfig: BodyConfig = BodyConfig(),
    dragConfig: DragConfig? = null,
) = composed {
    val bodyId = id ?: remember { UUID.randomUUID().toString() }
    val simulation = LocalSimulation.current
    val layoutToSimulation = LocalLayoutToSimulation.current
    val simulationToLayout = LocalSimulationToLayout.current
    val layoutOffset = remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(id) {
        onDispose {
            simulation.syncSimulationBody(bodyId, null)
        }
    }

    onPlaced { coordinates ->
        val (body, offsetFromCenter) = layoutToSimulation.convertBody(
            coordinates = coordinates,
            shape = shape,
            bodyConfig = bodyConfig,
        )
        layoutOffset.value = offsetFromCenter
        simulation.syncSimulationBody(bodyId, body)
    }.graphicsLayer {
        simulation.transformations[bodyId]?.let {
            val transformation = simulationToLayout.convertTransformation(
                offset = layoutOffset.value,
                simulationTransformation = it
            )
            translationX = transformation.translationX
            translationY = transformation.translationY
            rotationZ = transformation.rotation
        }
    }.then(
        if (dragConfig != null) {
            touch {
                simulation.drag(
                    bodyId = bodyId,
                    touchEvent = layoutToSimulation.convertTouchEvent(it),
                    dragConfig = dragConfig
                )
            }
        } else Modifier
    )
}

/**
 * Configures properties of the body
 */
@Immutable
data class BodyConfig(
    /**
     * Whether or not this body is movable. Set to `false` for walls or floors
     */
    val isStatic: Boolean = false,

    /**
     * The angular damping, see [org.dyn4j.dynamics.PhysicsBody.setAngularDamping]
     */
    val angularDamping: Float = 0.7f,

    /**
     * The density, see [org.dyn4j.dynamics.PhysicsBody.addFixture]
     */
    val density: Float = 1.0f,

    /**
     * The friction, see [org.dyn4j.dynamics.PhysicsBody.addFixture]
     */
    val friction: Float = 0.2f,

    /**
     * The restitution, see [org.dyn4j.dynamics.PhysicsBody.addFixture]
     */
    val restitution: Float = 0.4f,
)

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)