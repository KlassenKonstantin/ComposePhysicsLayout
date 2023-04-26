package de.apuri.physicslayout.lib2
import org.dyn4j.dynamics.Body as LibBody

internal sealed class SimulationEntity : LibBody() {

    class Body : SimulationEntity() {
        fun getTransformation() = SimulationTransformation(
            translationX = transform.translationX,
            translationY = transform.translationY,
            rotation = transform.rotation.toDegrees()
        )
    }

    class Border : SimulationEntity()
}