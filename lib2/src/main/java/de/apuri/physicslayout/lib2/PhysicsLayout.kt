@file:Suppress("PrivatePropertyName")

package de.apuri.physicslayout.lib2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib2.conversion.LayoutToSimulation
import de.apuri.physicslayout.lib2.conversion.SimulationToLayout
import de.apuri.physicslayout.lib2.simulation.Simulation
import de.apuri.physicslayout.lib2.simulation.rememberSimulation
import java.util.UUID

private val DEFAULT_SCALE = 64.dp
internal const val TAG = "PhysicsLayout"

@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    shape: Shape? = RectangleShape,
    scale: Dp = DEFAULT_SCALE,
    simulation: Simulation = rememberSimulation(),
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val scalePx = density.run { scale.toPx().toDouble() }

    val layoutToSimulation = remember(density) {
        LayoutToSimulation(density, scalePx)
    }

    val simulationToLayout = remember(density) {
        SimulationToLayout(density, scalePx)
    }

    CompositionLocalProvider(
        LocalSimulation provides simulation,
        LocalLayoutToSimulation provides layoutToSimulation,
        LocalSimulationToLayout provides simulationToLayout,
    ) {
        Box(
            modifier = modifier.physicsBorder(shape, simulation, layoutToSimulation),
            content = content
        )
    }
}

val LocalSimulation = staticCompositionLocalOf<Simulation> {
    throw IllegalStateException("No Simulation provided")
}

private val LocalLayoutToSimulation = staticCompositionLocalOf<LayoutToSimulation> {
    throw IllegalStateException("No LayoutToSimulation provided")
}

private val LocalSimulationToLayout = staticCompositionLocalOf<SimulationToLayout> {
    throw IllegalStateException("No LayoutToSimulation provided")
}

fun Modifier.physicsBody(
    id: String? = null,
    shape: Shape = RectangleShape,
    bodyConfig: BodyConfig,
) = composed {
    val bodyId = id ?: remember { UUID.randomUUID().toString() }
    val simulation = LocalSimulation.current
    val layoutToSimulation = LocalLayoutToSimulation.current
    val simulationToLayout = LocalSimulationToLayout.current
    val layoutOffset = remember { mutableStateOf(Offset.Zero) }

    onPlaced { coordinates ->
        val (body, offsetFromCenter) = layoutToSimulation.convertBody(
            coordinates,
            shape,
            bodyConfig,
        )
        layoutOffset.value = offsetFromCenter
        simulation.syncSimulationBody(bodyId, body)
    }.graphicsLayer {
        simulation.transformations[bodyId]?.let {
            val layoutTransformation = simulationToLayout.convertTransformation(layoutOffset.value, it)
            translationX = layoutTransformation.translationX
            translationY = layoutTransformation.translationY
            rotationZ = layoutTransformation.rotation
        }
    }
}

internal fun Modifier.physicsBorder(
    shape: Shape? = RectangleShape,
    simulation: Simulation,
    layoutToSimulation: LayoutToSimulation,
) = onPlaced {
    layoutToSimulation.containerLayoutCoordinates = it
    val simulationBorder = layoutToSimulation.convertBorder(it.size, shape)
    simulation.syncSimulationBorder(simulationBorder)
}

@Immutable
internal data class LayoutTransformation(
    val translationX: Float,
    val translationY: Float,
    val rotation: Float,
)

@Immutable
data class BodyConfig(
    val isStatic: Boolean = false,
    val angularDamping: Float = 0.7f,
    val density: Float = 1.0f,
    val friction: Float = 0.2f,
    val restitution: Float = 0.4f,
)