package de.apuri.physicslayout.lib2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import de.apuri.physicslayout.lib2.conversion.LayoutToSimulation
import de.apuri.physicslayout.lib2.simulation.Simulation

internal fun Modifier.physicsBorder(
    shape: Shape? = RectangleShape,
    simulation: Simulation,
    layoutToSimulation: LayoutToSimulation,
) = composed {
    var containerLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    containerLayoutCoordinates?.let {
        val simulationBorder = layoutToSimulation.convertBorder(it.size, shape)
        simulation.syncSimulationBorder(simulationBorder)
    }

    onPlaced {
        layoutToSimulation.containerLayoutCoordinates = it
        containerLayoutCoordinates = it
    }
}