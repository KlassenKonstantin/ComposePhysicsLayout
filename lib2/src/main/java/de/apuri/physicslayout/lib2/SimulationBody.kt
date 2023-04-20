package de.apuri.physicslayout.lib2
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import org.dyn4j.geometry.Vector2
import java.util.Vector
import org.dyn4j.dynamics.Body as LibBody

internal class SimulationBody(
    var width: Double = 0.0,
    var height: Double = 0.0,
    val offset: Vector2 = Vector2(),
) : LibBody() {

}