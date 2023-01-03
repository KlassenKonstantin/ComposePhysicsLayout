package de.apuri.physicslayout.samples

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.joint.Joint
import de.apuri.physicslayout.lib.layout.PhysicsLayout
import de.apuri.physicslayout.lib.rememberSimulation

@Composable
fun RopeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val simulation = rememberSimulation()

        GravitySensor {
            simulation.setGravity(it.copy(x = -it.x).times(3f))
        }

        Box {
            PhysicsLayout(
                onBodiesAdded = { allBodies, addedBodies ->
                    if (addedBodies.isEmpty()) {
                        return@PhysicsLayout
                    }

                    simulation.addJoint(
                        Joint.RevoluteJoint(
                            idA = addedBodies[0].id,
                            idB = addedBodies[1].id,
                            anchorARel = Offset(0f, 0f)
                        )
                    )

                    addedBodies.filter { it.id.startsWith("rope") }.windowed(2) { (bodyA, bodyB) ->
                        simulation.addJoint(
                            Joint.RevoluteJoint(
                                idA = bodyA.id,
                                idB = bodyB.id,
                                anchorARel = Offset(0f, 1f)
                            )
                        )
                    }
                },
                simulation = simulation
            ) {
                var dragConfig by remember { mutableStateOf<DragConfig>(DragConfig.NotDraggable) }
                val ropeSegments = 30
                val width = 16.dp
                val height = 20.dp
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .zIndex(1f)
                        .body(
                            shape = CircleShape,
                            isStatic = dragConfig is DragConfig.NotDraggable,
                            dragConfig = dragConfig,
                            initialTranslation = DpOffset(
                                0.dp,
                                -350.dp
                            )
                        )
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                awaitFirstDown()
                                dragConfig = DragConfig.Draggable()
                            }
                        },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCA28))
                ) {

                }
                (0 until ropeSegments).forEach {
                    Box(
                        modifier = Modifier
                            .background(lerp(Color(0xFFEF5350), Color(0xFFFFCA28), it.toFloat() / ropeSegments))
                            .size(width, height)
                            .body(
                                id = "rope$it",
                                initialTranslation = DpOffset(
                                    0.dp,
                                    -350.dp + height / 2 + (height * it)
                                ),
                                dragConfig = DragConfig.NotDraggable
                            )
                    )
                }

                Card(
                    modifier = Modifier
                        .size(128.dp)
                        .body(
                            shape = CircleShape,
                            isStatic = true,
                            initialTranslation = DpOffset(
                                -80.dp,
                                -250.dp
                            )
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9CCC65))
                ) {

                }

                Card(
                    modifier = Modifier
                        .size(64.dp)
                        .body(
                            shape = CircleShape,
                            isStatic = true,
                            initialTranslation = DpOffset(
                                100.dp,
                                -100.dp
                            )
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9CCC65))
                ) {

                }

                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .body(
                            shape = CircleShape,
                            isStatic = true,
                            initialTranslation = DpOffset(
                                -70.dp,
                                100.dp
                            )
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9CCC65))
                ) {

                }
            }
        }
    }
}