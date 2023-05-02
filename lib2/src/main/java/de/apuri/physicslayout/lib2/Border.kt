package de.apuri.physicslayout.lib2

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onPlaced
import de.apuri.physicslayout.lib2.conversion.LocalLayoutToSimulation
import de.apuri.physicslayout.lib2.simulation.Simulation

@Stable
internal fun Modifier.physicsBorder(
    shape: Shape? = RectangleShape,
    simulation: Simulation,
) = composed {
    val layoutToSimulation = LocalLayoutToSimulation.current

    layoutToSimulation.containerLayoutCoordinates.value?.let {
        val simulationBorder = layoutToSimulation.convertBorder(it.size, shape)
        simulation.syncSimulationBorder(simulationBorder)
    }

    onPlaced {
        layoutToSimulation.containerLayoutCoordinates.value = it
    }
}