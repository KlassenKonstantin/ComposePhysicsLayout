package de.apuri.physicslayout.lib2

internal fun Body.updateFrom(border: SimulationBorder) {
    width = border.width
    height = border.height

    removeAllFixtures()
    border.shape?.toSimulationBorderFixtures()?.forEach(this::addFixture)
    updateMass()
}