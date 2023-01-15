@file:OptIn(ExperimentalMaterial3Api::class)

package de.apuri.physicslayout.samples

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.PhysicsLayoutScope
import de.apuri.physicslayout.lib.rememberSimulation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StarLauncherScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val simulation = rememberSimulation()
        var starCounter by remember { mutableStateOf(0) }
        val stars = remember { mutableStateListOf<StarMeta>() }

        val redCount = remember {
            derivedStateOf { stars.count { it.color == red } }
        }

        val purpleCount = remember {
            derivedStateOf { stars.count { it.color == purple } }
        }

        val blueCount = remember {
            derivedStateOf { stars.count { it.color == blue } }
        }

        val greenCount = remember {
            derivedStateOf { stars.count { it.color == green } }
        }

        GravitySensor {
            simulation.setGravity(it.copy(x = -it.x).times(3f))
        }

        Box {
            PhysicsLayout(
                modifier = Modifier.systemBarsPadding(),
                onBodiesAdded = { allBodies, addedBodies ->
//                                val stars = allBodies.filter { it.id.startsWith("star") }
//                                if (stars.size > 1) {
//                                    simulation.addJoint(
//                                        Joint.DistanceJoint(
//                                            stars[stars.size - 2].id,
//                                            addedBodies.first().id,
//                                            lowerLimit = 48.dp,
//                                            upperLimit = 96.dp
//                                        )
//                                    )
//                                }
                },
                simulation = simulation
            ) {
                stars.forEach { starMeta ->
                    Star(
                        id = starMeta.id,
                        color = starMeta.color,
                        offset = starMeta.initialOffset
                    ) { id ->
                        stars.removeIf { it.id == id }
                    }
                }

                StarCounterContainer(
                    { redCount.value },
                    { purpleCount.value },
                    { blueCount.value },
                    { greenCount.value },
                )

                val launchOffset = DpOffset(0.dp, -300.dp)

                StarLauncher(
                    color = red,
                    offset = DpOffset(-120.dp, 225.dp)
                ) {
                    stars.add(StarMeta("star-${starCounter++}", red, launchOffset))
                }

                StarLauncher(
                    color = purple,
                    offset = DpOffset(0.dp, 300.dp)
                ) {
                    stars.add(StarMeta("star-${starCounter++}", purple, launchOffset))
                }

                StarLauncher(
                    color = blue,
                    offset = DpOffset(120.dp, 225.dp)
                ) {
                    stars.add(StarMeta("star-${starCounter++}", blue, launchOffset))
                }

                StarLauncher(
                    color = green,
                    offset = DpOffset(0.dp, 150.dp)
                ) {
                    stars.add(StarMeta("star-${starCounter++}", green, launchOffset))
                }
            }
        }
    }
}

@Composable
fun PhysicsLayoutScope.StarLauncher(
    color: Color,
    offset: DpOffset,
    onStar: (DpOffset) -> Unit
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .size(64.dp)
            .body(
                shape = CircleShape,
                isStatic = true,
                initialTranslation = offset,
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val job = scope.launch {
                            while (true) {
                                onStar(offset)
                                delay(100)
                            }
                        }
                        tryAwaitRelease()
                        job.cancel()
                    }
                )
            },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Default.Add,
                contentDescription = "Add red"
            )
        }
    }
}

@Composable
fun PhysicsLayoutScope.StarCounterContainer(
    provideRedCount: () -> Int,
    providePurpleCount: () -> Int,
    provideBlueCount: () -> Int,
    provideGreenCount: () -> Int,
) {
    var dragConfig by remember { mutableStateOf<DragConfig>(DragConfig.NotDraggable) }

    Card(
        modifier = Modifier
            .body(
                shape = RoundedCornerShape(16.dp),
                isStatic = dragConfig is DragConfig.NotDraggable,
                dragConfig = dragConfig,
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    awaitFirstDown()
                    dragConfig = DragConfig.Draggable()
                }
            },
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StarCounter(color = red, provideCount = provideRedCount)
            StarCounter(color = purple, provideCount = providePurpleCount)
            StarCounter(color = blue, provideCount = provideBlueCount)
            StarCounter(color = green, provideCount = provideGreenCount)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StarCounter(color: Color, provideCount: () -> Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier,
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp),
                imageVector = Icons.Default.Star,
                contentDescription = "",
                tint = Color.White
            )
        }

        AnimatedContent(
            targetState = provideCount(),
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height / 3 } + fadeIn() with
                            slideOutVertically { height -> -height / 3 } + fadeOut()
                } else {
                    slideInVertically { height -> -height / 3 } + fadeIn() with
                            slideOutVertically { height -> height / 3 } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "$it",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun PhysicsLayoutScope.Star(
    id: String,
    color: Color,
    offset: DpOffset,
    onClick: (String) -> Unit
) {
    key(id) {
        Card(
            modifier = Modifier.body(
                id = id,
                shape = CircleShape,
                initialTranslation = offset,
                dragConfig = DragConfig.Draggable()
            ),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = color),
            onClick = { onClick(id) }
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp),
                imageVector = Icons.Default.Star,
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}

@Immutable
data class StarMeta(
    val id: String,
    val color: Color,
    val initialOffset: DpOffset
)

private val red = Color(0xFFEF5350)
private val purple = Color(0xFFAB47BC)
private val blue = Color(0xFF42A5F5)
private val green = Color(0xFF66BB6A)