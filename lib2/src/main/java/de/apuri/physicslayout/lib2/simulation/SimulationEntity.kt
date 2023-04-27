package de.apuri.physicslayout.lib2.simulation
import android.util.Log
import org.dyn4j.geometry.MassType
import org.dyn4j.dynamics.Body as LibBody

internal sealed class SimulationEntity<T> : LibBody() {

    protected abstract fun updateFrom(current: T?, new: T)

    fun updateFrom(new: T) = updateFrom(this.userData as? T, new)

    class Body : SimulationEntity<SimulationBody>() {
        fun getTransformation() = SimulationTransformation(
            translationX = transform.translationX,
            translationY = transform.translationY,
            rotation = transform.rotation.toDegrees()
        )

        override fun updateFrom(current: SimulationBody?, new: SimulationBody) {
            angularDamping = new.bodyConfig.angularDamping.toDouble()

            if (new.shape != current?.shape) {
                removeAllFixtures()
                new.shape.toSimulationBodyFixtures().forEach {
                    addFixture(
                        it,
                        new.bodyConfig.density.toDouble(),
                        new.bodyConfig.friction.toDouble(),
                        new.bodyConfig.restitution.toDouble(),
                    )
                }
                setMass(if (new.bodyConfig.isStatic) MassType.INFINITE else MassType.NORMAL)
            } else if (new.bodyConfig != current.bodyConfig) {
                fixtures.forEach {
                    it.density = new.bodyConfig.density.toDouble()
                    it.friction = new.bodyConfig.friction.toDouble()
                    it.restitution = new.bodyConfig.restitution.toDouble()
                }
            }
        }
    }

    class Border : SimulationEntity<SimulationBorder>() {
        override fun updateFrom(current: SimulationBorder?, new: SimulationBorder) {
            removeAllFixtures()
            new.shape?.toSimulationBorderFixtures()?.forEach {
                addFixture(it)
            }
            updateMass()
        }
    }
}