package de.apuri.physicslayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.PhysicsLayoutScope
import de.apuri.physicslayout.lib.rememberSimulation
import de.apuri.physicslayout.ui.theme.PhysicsLayoutTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            PhysicsLayoutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val simulation = rememberSimulation()
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
                            simulation = simulation
                        ) {
                            stars.forEach { starMeta ->
                                Star(
                                    color = starMeta.color,
                                    offset = starMeta.initialOffset
                                )
                            }

                            StarCounterContainer(
                                { redCount.value },
                                { purpleCount.value },
                                { blueCount.value },
                                { greenCount.value },
                            )

                            val launchOffset = LocalDensity.current.run { Offset(0f, -300.dp.toPx()) }

                            StarLauncher(
                                color = red,
                                offset = LocalDensity.current.run { Offset(-120.dp.toPx(), 225.dp.toPx()) }
                            ) {
                                stars.add(StarMeta(red, launchOffset))
                            }

                            StarLauncher(
                                color = purple,
                                offset = LocalDensity.current.run { Offset(0f, 300.dp.toPx()) }
                            ) {
                                stars.add(StarMeta(purple, launchOffset))
                            }

                            StarLauncher(
                                color = blue,
                                offset = LocalDensity.current.run { Offset(120.dp.toPx(), 225.dp.toPx()) }
                            ) {
                                stars.add(StarMeta(blue, launchOffset))
                            }

                            StarLauncher(
                                color = green,
                                offset = LocalDensity.current.run { Offset(0.dp.toPx(), 150.dp.toPx()) }
                            ) {
                                stars.add(StarMeta(green, launchOffset))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicsLayoutScope.StarLauncher(
    color: Color,
    offset: Offset,
    onStar: (Offset) -> Unit
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .size(64.dp)
            .body(
                shape = CircleShape,
                isStatic = true,
                initialTranslation = offset
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
    Card(
        modifier = Modifier
            .body(
                shape = RoundedCornerShape(8.dp),
                isStatic = true
            )
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
                    slideInVertically { height -> height/3 } + fadeIn() with
                            slideOutVertically { height -> -height/3 } + fadeOut()
                } else {
                    slideInVertically { height -> -height/3 } + fadeIn() with
                            slideOutVertically { height -> height/3 } + fadeOut()
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
    color: Color,
    offset: Offset
) {
    Card(
        modifier = Modifier.body(
            shape = CircleShape,
            initialTranslation = Offset(offset.x, offset.y),
            initialImpulse = Offset((Random.nextFloat() - 0.5f) * 2, (Random.nextFloat()) * 2)
        ),
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
}

@Immutable
data class StarMeta(
    val color: Color,
    val initialOffset: Offset
)

private val red = Color(0xFFEF5350)
private val purple = Color(0xFFAB47BC)
private val blue = Color(0xFF42A5F5)
private val green = Color(0xFF66BB6A)