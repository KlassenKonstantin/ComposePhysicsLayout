package de.apuri.physicslayout.lib2

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import org.dyn4j.geometry.Vector2

internal class SimulationToLayout(
    private val density: Density,
    private val scale: Double
) {
    private fun Double.toLayoutSize() = (this * scale).toFloat()

    fun convertTransformation(offset: Offset, simulationTransformation: SimulationTransformation) = LayoutTransformation(
        translationX = simulationTransformation.translationX.toLayoutSize() - offset.x,
        translationY = simulationTransformation.translationY.toLayoutSize() - offset.y,
        rotation = simulationTransformation.rotation.toFloat()
    )
}