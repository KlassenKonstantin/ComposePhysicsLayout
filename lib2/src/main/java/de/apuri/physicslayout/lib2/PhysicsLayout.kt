package de.apuri.physicslayout.lib2

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import java.util.UUID

@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    simulation: Simulation = rememberSimulation(),
    content: @Composable BoxScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalSimulation provides simulation,
    ) {
        Box(modifier = modifier.physicsBorder(), content = content)
    }
}

val LocalSimulation = staticCompositionLocalOf<Simulation> {
    throw IllegalStateException("No Simulation provided")
}

fun Modifier.physicsBody(
    id: String? = null,
    shape: Shape = RectangleShape,
) = composed {
    val bodyId = id ?: remember { UUID.randomUUID().toString() }
    val simulation = LocalSimulation.current

    onPlaced { layoutCoordinates ->
        //Log.d("asdf", "Place $bodyId with size: (${layoutCoordinates.size.width}, ${layoutCoordinates.size.height}) at pos: (${layoutCoordinates.positionInParent()})")
        Log.d("asdf", "BODY")
        simulation.syncPhysicsBody(
            id = bodyId,
            PhysicsBody(
                width = layoutCoordinates.size.width,
                height = layoutCoordinates.size.height,
                shape = shape,
                layoutCoordinates = layoutCoordinates
            )
        )
    }.graphicsLayer {
        simulation.transformations[bodyId]?.let {
            translationX = it.translationX
            translationY = it.translationY
            rotationZ = it.rotation
        }
    }
}

internal fun Modifier.physicsBorder(
    shape: Shape = RectangleShape,
) = composed {
    val simulation = LocalSimulation.current
    DisposableEffect(Unit) {
        onDispose {
            simulation.syncPhysicsBorder(null)
        }
    }

    onPlaced {
        Log.d("asdf", "BORDER")
        simulation.syncPhysicsBorder(
            PhysicsBorder(
                width = it.size.width,
                height = it.size.height,
                shape = shape,
                layoutCoordinates = it
            )
        )
    }
}