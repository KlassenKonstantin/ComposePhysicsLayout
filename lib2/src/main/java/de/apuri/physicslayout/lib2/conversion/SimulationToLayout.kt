package de.apuri.physicslayout.lib2.conversion

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import de.apuri.physicslayout.lib2.LayoutTransformation
import de.apuri.physicslayout.lib2.simulation.SimulationTransformation

internal class SimulationToLayout(
    private val scale: Double
) {
    private fun Double.toLayoutSize() = (this * scale).toFloat()

    fun convertTransformation(offset: Offset, simulationTransformation: SimulationTransformation) = LayoutTransformation(
        translationX = simulationTransformation.translationX.toLayoutSize() - offset.x,
        translationY = simulationTransformation.translationY.toLayoutSize() - offset.y,
        rotation = simulationTransformation.rotation.toFloat()
    )
}