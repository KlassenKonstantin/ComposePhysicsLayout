package de.apuri.physicslayout.lib.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.WorldBody
import de.apuri.physicslayout.lib.shape.toBodyShape
import de.apuri.physicslayout.lib.toWorldSize
import de.apuri.physicslayout.lib.toWorldVector2

data class LayoutBody(
    val id: String,
    val width: Int,
    val height: Int,
    val shape: Shape,
    val isStatic: Boolean,
    val initialTranslation: Offset
)

context(Simulation)
internal fun LayoutBody.toWorldBody() = WorldBody(
    id = id,
    width = width.toWorldSize(),
    height = height.toWorldSize(),
    shape = toBodyShape(),
    isStatic = isStatic,
    initialTranslation = initialTranslation.toWorldVector2()
)

context(Simulation)
internal fun List<LayoutBody>.toWorldBodies() = map {
    it.toWorldBody()
}