package de.apuri.physicslayout.lib

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * A layout backed by a physics engine. All Composables must use the [PhysicsLayoutScope.body] modifier,
 * or else an Exception is thrown.
 */
@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    simulation: Simulation,
    content: @Composable PhysicsLayoutScope.() -> Unit,
) {
    Layout(
        modifier = modifier.onSizeChanged(simulation::updateWorldSize),
        content = { PhysicsLayoutScopeInstance.content() }
    ) { measurables, constraints: Constraints ->
        check(measurables.none { it.parentData as? BodyChildData == null } ) {
            "All Composables must use the body modifier"
        }

        val placeables = measurables.map { it.measure(constraints) }

        val layoutItems = placeables.map { placeable ->
            val childData = placeable.parentData as BodyChildData
            LayoutItem(
                id = childData.id,
                width = placeable.width,
                height = placeable.height,
                shape = childData.shape,
                isStatic = childData.isStatic,
                initialTranslation = childData.initialTranslation,
                initialImpulse = childData.initialImpulse
            )
        }

        simulation.syncBodies(layoutItems)

        layout(constraints.maxWidth, constraints.maxHeight) {
            val halfWidth = constraints.maxWidth / 2
            val halfHeight = constraints.maxHeight / 2

            placeables.forEach { placeable ->
                val childData = placeable.parentData as BodyChildData

                placeable.placeWithLayer(
                    x = halfWidth - placeable.measuredWidth / 2,
                    y = halfHeight - placeable.measuredHeight / 2,
                ) {
                    simulation.transformations[childData.id]?.let {
                        translationX = it.translationX
                        translationY = it.translationY
                        rotationZ = it.rotation
                    }
                }
            }
        }
    }
}

private class BodyChildData(
    val id: String,
    val shape: RoundedCornerShape,
    val isStatic: Boolean,
    val initialTranslation: Offset,
    val initialImpulse: Offset,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@BodyChildData
}

@LayoutScopeMarker
@Immutable
interface PhysicsLayoutScope {

    /**
     * Meta data that describes this Composable's bounds and behavior in the physics world.
     *
     * @id: The id the body should have in the simulation.
     *      Useful for operations directly on bodies (not yet supported).
     * @shape: Describes the outer bounds of the body. Only [RoundedCornerShape]s are supported.
     * @isStatic: Set true for unmovable bodies like walls and floors.
     * @initialTranslation: Where this body should be placed in the layout.
     *                      An Offset of (0,0) is the center of the layout, not top left.
     * @initialImpulse: The impulse that should be applied to this body once it's placed into the world
     */
    @Stable
    fun Modifier.body(
        id: String? = null,
        shape: RoundedCornerShape = RoundedCornerShape(0.dp),
        isStatic: Boolean = false,
        initialTranslation: Offset = Offset.Zero,
        initialImpulse: Offset = Offset.Zero,
    ): Modifier
}

private object PhysicsLayoutScopeInstance : PhysicsLayoutScope {
    @Stable
    override fun Modifier.body(
        id: String?,
        shape: RoundedCornerShape,
        isStatic: Boolean,
        initialTranslation: Offset,
        initialImpulse: Offset,
    ) = composed {
        val bodyId = id ?: remember { UUID.randomUUID().toString() }
        BodyChildData(
            id = bodyId,
            shape = shape,
            isStatic = isStatic,
            initialTranslation = initialTranslation,
            initialImpulse = initialImpulse
        )
    }
}