package de.apuri.physicslayout.lib.body
import org.dyn4j.dynamics.Body as LibBody

class Body(
    width: Double = 0.0,
    height: Double = 0.0,
) : LibBody() {
    var width: Double = width
    private set

    var height: Double = height
    private set

    fun updateSize(newWidth: Double, newHeight: Double) {
        width = newWidth
        height = newHeight
    }
}