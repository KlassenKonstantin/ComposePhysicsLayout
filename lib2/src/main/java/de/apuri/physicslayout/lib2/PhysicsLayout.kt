@file:Suppress("PrivatePropertyName")

package de.apuri.physicslayout.lib2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.UUID

private val DEFAULT_SCALE = 64.dp
internal const val TAG = "PhysicsLayout"

@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    shape: Shape?,
    scale: Dp = DEFAULT_SCALE,
    simulation: Simulation = rememberSimulation(),
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val scalePx = density.run { scale.toPx().toDouble() }

    val layoutToSimulation = remember(density) {
        LayoutToSimulation(density, scalePx)
    }

    CompositionLocalProvider(
        LocalSimulation provides simulation,
        LocalLayoutToSimulation provides layoutToSimulation
    ) {
        Box(modifier = modifier.physicsBorder(shape, simulation, layoutToSimulation), content = content)
    }
}

private val LocalSimulation = staticCompositionLocalOf<Simulation> {
    throw IllegalStateException("No Simulation provided")
}

private val LocalLayoutToSimulation = staticCompositionLocalOf<LayoutToSimulation> {
    throw IllegalStateException("No LayoutToSimulation provided")
}

fun Modifier.physicsBody(
    id: String? = null,
    shape: Shape = RectangleShape,
) = composed {
    val bodyId = id ?: remember { UUID.randomUUID().toString() }
    val simulation = LocalSimulation.current

    onPlaced { layoutCoordinates ->
//        //Log.d("asdf", "Place $bodyId with size: (${layoutCoordinates.size.width}, ${layoutCoordinates.size.height}) at pos: (${layoutCoordinates.positionInParent()})")
//        simulation.syncPhysicsBody(
//            id = bodyId,
//            LayoutBody(
//                width = layoutCoordinates.size.width,
//                height = layoutCoordinates.size.height,
//                shape = shape,
//                layoutCoordinates = layoutCoordinates
//            )
//        )
//    }.graphicsLayer {
//        simulation.transformations[bodyId]?.let {
//            translationX = it.translationX
//            translationY = it.translationY
//            rotationZ = it.rotation
//        }
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