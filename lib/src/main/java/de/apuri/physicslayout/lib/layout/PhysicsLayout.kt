package de.apuri.physicslayout.lib.layout

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.drag.touch
import de.apuri.physicslayout.lib.rememberSimulation
import java.util.UUID

/**
 * A layout backed by a physics engine. All Composables must use the [PhysicsLayoutScope.body] modifier,
 * or else an Exception is thrown.
 */
@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    simulation: Simulation = rememberSimulation(),
    onBodiesAdded: OnBodiesAdded? = null,
    shape: Shape = CircleShape,
    content: @Composable PhysicsLayoutScope.() -> Unit,
) {
    val layoutBodySyncManager = remember { LayoutBodySyncManager() }
    val density = LocalDensity.current

    Layout(
        modifier = modifier.onSizeChanged(simulation::updateWorldSize),
        content = { remember(simulation) { PhysicsLayoutScopeInstance(simulation) }.content() }
    ) { measurables, constraints: Constraints ->
        check(measurables.none { it.parentData as? BodyChildData == null } ) {
            "All Composables must use the body modifier"
        }

        val placeables = measurables.map { it.measure(constraints) }

        val layoutBodies = placeables.map { placeable ->
            val childData = placeable.parentData as BodyChildData
            LayoutBody(
                id = childData.id,
                width = placeable.width,
                height = placeable.height,
                shape = childData.shape,
                isStatic = childData.isStatic,
                initialTranslation = density.run { Offset(childData.initialTranslation.x.toPx(), childData.initialTranslation.y.toPx()) }
            )
        }

        layoutBodySyncManager.syncBodies(layoutBodies).also { syncResult ->
            simulation.applySyncResult(syncResult)
            onBodiesAdded?.invoke(layoutBodies, syncResult.added)
        }

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

fun interface OnBodiesAdded {
    operator fun invoke(allBodies: List<LayoutBody>, addedBodies: List<LayoutBody>)
}

private class BodyChildData(
    val id: String,
    val shape: Shape,
    val isStatic: Boolean,
    val initialTranslation: DpOffset
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@BodyChildData
}

@LayoutScopeMarker
@Immutable
interface PhysicsLayoutScope {

    /**
     * Meta data that describes this Composable's bounds and behavior in the physics world.
     */
    @Stable
    fun Modifier.body(
        /**
         * The id the body should have in the simulation.
         * Useful for operations that act directly on bodies (not yet supported).
         */
        id: String? = null,

        /**
         * Describes the outer bounds of the body.
         */
        shape: Shape = RoundedCornerShape(0.dp),

        /**
         * Set true for unmovable bodies like walls and floors.
         */
        isStatic: Boolean = false,

        /**
         * Where this body should be placed in the layout.
         * An Offset of (0,0) is the center of the layout, not top left.
         */
        initialTranslation: DpOffset = DpOffset.Zero,

        /**
         * Set to [DragConfig.Draggable] to enable drag support
         */
        dragConfig: DragConfig = DragConfig.NotDraggable
    ): Modifier
}

private class PhysicsLayoutScopeInstance(
    private val simulation: Simulation
) : PhysicsLayoutScope {
    @Stable
    override fun Modifier.body(
        id: String?,
        shape: Shape,
        isStatic: Boolean,
        initialTranslation: DpOffset,
        dragConfig: DragConfig
    ) = composed {
        val bodyId = id ?: remember { UUID.randomUUID().toString() }
        BodyChildData(
            id = bodyId,
            shape = shape,
            isStatic = isStatic,
            initialTranslation = initialTranslation
        ).then(
            if(dragConfig is DragConfig.Draggable) {
                touch { simulation.drag(bodyId, it, dragConfig) }
            } else {
                Modifier
            }
        )
    }
}