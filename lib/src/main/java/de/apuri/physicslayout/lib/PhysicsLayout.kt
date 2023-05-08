@file:Suppress("PrivatePropertyName")

package de.apuri.physicslayout.lib

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.conversion.LayoutToSimulation
import de.apuri.physicslayout.lib.conversion.LocalLayoutToSimulation
import de.apuri.physicslayout.lib.conversion.LocalSimulationToLayout
import de.apuri.physicslayout.lib.conversion.SimulationToLayout
import de.apuri.physicslayout.lib.simulation.Simulation
import de.apuri.physicslayout.lib.simulation.rememberSimulation

private val DEFAULT_SCALE = 32.dp

/**
 * This is the entry to the physics world. [shape] defines the border of the simulation or `null` if no borders are
 * wanted. [scale] defines how many [Dp] should be considered one meter. Bodies should not be too small. As a rule of
 * thumb use at least one meter for width and height.
 */
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

    val simulationToLayout = remember(scalePx) {
        SimulationToLayout(scalePx)
    }

    CompositionLocalProvider(
        LocalSimulation provides simulation,
        LocalLayoutToSimulation provides layoutToSimulation,
        LocalSimulationToLayout provides simulationToLayout,
    ) {
        Box(
            modifier = modifier.physicsBorder(
                shape = shape,
                simulation = simulation,
            ),
            content = content,
        )
    }
}

val LocalSimulation = staticCompositionLocalOf<Simulation> {
    throw IllegalStateException("No Simulation provided")
}