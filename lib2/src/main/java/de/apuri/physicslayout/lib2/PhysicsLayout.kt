@file:Suppress("PrivatePropertyName")

package de.apuri.physicslayout.lib2

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
import de.apuri.physicslayout.lib2.conversion.LayoutToSimulation
import de.apuri.physicslayout.lib2.conversion.SimulationToLayout
import de.apuri.physicslayout.lib2.simulation.Simulation
import de.apuri.physicslayout.lib2.simulation.rememberSimulation

private val DEFAULT_SCALE = 32.dp

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
                layoutToSimulation = layoutToSimulation
            ),
            content = content,
        )
    }
}

val LocalSimulation = staticCompositionLocalOf<Simulation> {
    throw IllegalStateException("No Simulation provided")
}

internal val LocalLayoutToSimulation = staticCompositionLocalOf<LayoutToSimulation> {
    throw IllegalStateException("No LayoutToSimulation provided")
}

internal val LocalSimulationToLayout = staticCompositionLocalOf<SimulationToLayout> {
    throw IllegalStateException("No LayoutToSimulation provided")
}