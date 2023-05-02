package de.apuri.physicslayout.lib2

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import de.apuri.physicslayout.lib2.conversion.LocalLayoutToSimulation
import de.apuri.physicslayout.lib2.conversion.LocalSimulationToLayout
import de.apuri.physicslayout.lib2.drag.DragConfig
import de.apuri.physicslayout.lib2.drag.touch
import java.util.UUID

@Stable
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

@Immutable
data class BodyConfig(
    val isStatic: Boolean = false,
    val angularDamping: Float = 0.7f,
    val density: Float = 1.0f,
    val friction: Float = 0.2f,
    val restitution: Float = 0.4f,
)

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)