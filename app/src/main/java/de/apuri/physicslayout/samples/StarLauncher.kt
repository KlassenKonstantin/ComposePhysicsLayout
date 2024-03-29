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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib.BodyConfig
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.physicsBody
import de.apuri.physicslayout.lib.simulation.rememberSimulation
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

        GravitySensor { (x, y) ->
            simulation.setGravity(Offset(-x, y).times(3f))
        }

        PhysicsLayout(
            modifier = Modifier.systemBarsPadding(),
            simulation = simulation,
            shape = RoundedCornerShape(64.dp)
        ) {
            stars.forEach { starMeta ->
                key(starMeta.id) {
                    Star(
                        id = starMeta.id,
                        color = starMeta.color,
                    ) { id ->
                        stars.removeIf { it.id == id }
                    }
                }
            }

            StarCounterContainer(
                { redCount.value },
                { purpleCount.value },
                { blueCount.value },
                { greenCount.value },
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.Bottom)
            ) {
                StarLauncher(
                    color = blue,
                ) {
                    stars.add(StarMeta("star-${starCounter++}", blue))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(64.dp)
                ) {
                    StarLauncher(
                        color = red,
                    ) {
                        stars.add(StarMeta("star-${starCounter++}", red))
                    }

                    StarLauncher(
                        color = purple,
                    ) {
                        stars.add(StarMeta("star-${starCounter++}", purple))
                    }
                }

                StarLauncher(
                    color = green,
                ) {
                    stars.add(StarMeta("star-${starCounter++}", green))
                }
            }
        }

    }
}

@Composable
fun StarLauncher(
    color: Color,
    onStar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .physicsBody(
                shape = CircleShape,
                bodyConfig = BodyConfig(isStatic = true)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val job = scope.launch {
                            while (true) {
                                onStar()
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
            modifier = Modifier.size(64.dp)
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
fun BoxScope.StarCounterContainer(
    provideRedCount: () -> Int,
    providePurpleCount: () -> Int,
    provideBlueCount: () -> Int,
    provideGreenCount: () -> Int,
) {
    var dragConfig by remember { mutableStateOf<DragConfig?>(null) }

    Card(
        modifier = Modifier
            .align(Alignment.Center)
            .physicsBody(
                shape = RoundedCornerShape(16.dp),
                bodyConfig = BodyConfig(isStatic = dragConfig == null),
                dragConfig = dragConfig,
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    awaitFirstDown()
                    dragConfig = DragConfig()
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
fun BoxScope.Star(
    id: String,
    color: Color,
    onClick: (String) -> Unit
) {
    Box(
        Modifier
            .align(Alignment.TopCenter)
            .padding(top = 32.dp)
    ) {
        Card(
            modifier = Modifier
                .physicsBody(
                    id = id,
                    shape = CircleShape,
                    dragConfig = DragConfig()
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
)

private val red = Color(0xFFEF5350)
private val purple = Color(0xFFAB47BC)
private val blue = Color(0xFF42A5F5)
private val green = Color(0xFF66BB6A)