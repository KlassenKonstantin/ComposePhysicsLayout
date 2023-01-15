@file:OptIn(ExperimentalAnimationApi::class)

package de.apuri.physicslayout.samples

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.R
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.PhysicsLayoutScope
import de.apuri.physicslayout.lib.rememberSimulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


private val achievements = mapOf(
    "1" to Achievement(
        "1",
        "Take a cold shower",
        Animatable(1f),
        BallMeta(
            borderColors = listOf(
                Color(0xFF10B981),
                Color(0xFF34D399),
            ),
            containerColors = listOf(
                Color(0xFF059669),
                Color(0xFF059669),
            ),
            icon = Icons.Filled.ThumbUp
        )
    ),
    "2" to Achievement(
        "2",
        "Breathe in, breathe out",
        Animatable(1f),
        BallMeta(
            borderColors = listOf(
                Color(0xFFEC4899),
                Color(0xFFF472B6),
            ),
            containerColors = listOf(
                Color(0xFFDB2777),
                Color(0xFFDB2777),
            ),
            icon = Icons.Filled.Favorite
        )
    ),
    "3" to Achievement(
        "3",
        "Take the stairs",
        Animatable(0f),
        BallMeta(
            borderColors = listOf(
                Color(0xFFA855F7),
                Color(0xFFC084FC),
            ),
            containerColors = listOf(
                Color(0xFF9333EA),
                Color(0xFF9333EA),
            ),
            iconRes = R.drawable.baseline_gamepad_24
        )
    ),
    "4" to Achievement(
        "4",
        "Finish a book for once",
        Animatable(0f),
        BallMeta(
            borderColors = listOf(
                Color(0xFFF59E0B),
                Color(0xFFFBBF24),
            ),
            containerColors = listOf(
                Color(0xFFD97706),
                Color(0xFFD97706),
            ),
            iconRes = R.drawable.baseline_emoji_events_24
        )
    ),
    "5" to Achievement(
        "5",
        "Sleep at least 7 hours",
        Animatable(0f),
        BallMeta(
            borderColors = listOf(
                Color(0xFFEAB308),
                Color(0xFFFACC15),
            ),
            containerColors = listOf(
                Color(0xFFCA8A04),
                Color(0xFFCA8A04),
            ),
            iconRes = R.drawable.baseline_electric_bolt_24
        )
    ),
    "6" to Achievement(
        "6",
        "Enjoy nature",
        Animatable(0f),
        BallMeta(
            borderColors = listOf(
                Color(0xFF38BDF8),
                Color(0xFF0EA5E9),
            ),
            containerColors = listOf(
                Color(0xFF0284C7),
                Color(0xFF0284C7),
            ),
            iconRes = R.drawable.baseline_cruelty_free_24
        )
    ),
    "7" to Achievement(
        "7",
        "Be nice",
        Animatable(0f),
        BallMeta(
            borderColors = listOf(
                Color(0XFF22D3EE),
                Color(0XFF06B6D4),
            ),
            containerColors = listOf(
                Color(0XFF0891B2),
                Color(0XFF0891B2),
            ),
            iconRes = R.drawable.baseline_emoji_objects_24
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AchievementsScreen() {
    val achievements = remember { mutableStateMapOf<String, Achievement>().apply { putAll(achievements) } }
    val (unlocked, locked) = achievements.entries.partition { it.value.progress.targetValue >= 1f }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val enterOrchestrator = remember(scope) {
        EnterOrchestrator(scope).also { orch ->
            val initiallyUnlocked = unlocked.map { it.key }
            initiallyUnlocked.forEach { orch.add(it) }
        }
    }
    Box(
        Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            snackbarHost = {
               SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Achievements")
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        //containerColor = Color(0xfff1f1f1)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                )
            },
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding() + 64.dp)
            ) {
                item {
                    BoxWithConstraints(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val simulation = rememberSimulation()

                        GravitySensor {
                            simulation.setGravity(it.copy(x = -it.x).times(3f))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedContent(
                                targetState = unlocked.size,
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
                                Text(text = "$it", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Thin, fontSize = 72.sp)
                            }
                        }

                        val shape = CutCornerShape(10)
                        PhysicsLayout(
                            modifier = Modifier
                                .border(1.dp, Brush.verticalGradient(0.2f to Color.Transparent, 1f to Color(0xff71717A)), shape)
                                .clip(shape),
                            simulation = simulation,
                            shape = shape
                        ) {
                            achievements.values.filter { it.progress.targetValue >= 1f }.forEachIndexed { index, achievement ->
                                if (enterOrchestrator.state.containsKey(achievement.id)) {
                                    key(achievement.id) {
                                        Ball(ball = achievement.ball, enterOrchestrator.state[achievement.id]!!.value)
                                    }
                                }
                            }
                        }
                    }
                }

                if (unlocked.isNotEmpty()) {
                    item(key = "Unlocked") {
                        Text(modifier = Modifier
                            .padding(16.dp)
                            .animateItemPlacement(), text = "Unlocked", style = MaterialTheme.typography.titleMedium)
                    }
                    items(unlocked.toList(), key = { it.key }) {
                        AchievementProgressTracker(it.value, 1f) {}
                    }
                }

                if (locked.isNotEmpty()) {
                    item(key = "Locked") {
                        Text(modifier = Modifier
                            .padding(16.dp)
                            .animateItemPlacement(), text = "Locked", style = MaterialTheme.typography.titleMedium)
                    }
                    items(locked.toList(), key = { it.key }) { (id, achievement) ->
                        AchievementProgressTracker(achievement, 0.38f) {
                            scope.launch {
                                achievements[id]?.progress?.let {
                                    val target = it.targetValue + 0.25f
                                    launch {
                                        if (target >= 1f) {
                                            enterOrchestrator.add(id)
                                            snackbarHostState.showSnackbar("Achievement unlocked!")
                                        }
                                    }
                                    it.animateTo(target, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhysicsLayoutScope.Ball(ball: BallMeta, alpha: Float) {
    val shape = CutCornerShape(16.dp)
    Box(
        modifier = Modifier
            .size(48.dp)
            .alpha(alpha)
            .background(Brush.verticalGradient(ball.containerColors), shape)
            .border(BorderStroke(4.dp, Brush.verticalGradient(ball.borderColors)), shape)
            .body(
                shape = shape,
                dragConfig = DragConfig.Draggable(
                    frequency = 1.0,
                    maxForce = 10.0
                ),
                initialTranslation = DpOffset(0.dp, -100.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (ball.icon != null) {
            Icon(imageVector = ball.icon, contentDescription = "", tint = Color.White)
        } else if (ball.iconRes != null) {
            Icon(painter = painterResource(id = ball.iconRes), contentDescription = "", tint = Color.White)
        }
    }
}

@Composable
private fun Ball(ball: BallMeta) {
    Box(
        modifier = Modifier
            .scale(0.8f)
            .size(48.dp)
            .background(Brush.verticalGradient(ball.containerColors), CircleShape)
            .border(BorderStroke(4.dp, Brush.verticalGradient(ball.borderColors)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (ball.icon != null) {
            Icon(imageVector = ball.icon, contentDescription = "", tint = Color.White)
        } else if (ball.iconRes != null) {
            Icon(painter = painterResource(id = ball.iconRes), contentDescription = "", tint = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.AchievementProgressTracker(achievement: Achievement, alpha: Float, onClicked: () -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClicked() }
            .animateItemPlacement(),
        headlineText = {
           Text(text = achievement.name)
        },
        leadingContent = {
            Box(modifier = Modifier.alpha(alpha)) {
                Ball(achievement.ball)
            }
        },
        supportingText = {
            LinearProgressIndicator(modifier = Modifier.padding(vertical = 8.dp), progress = achievement.progress.value)
        },
        trailingContent = {
            val format = NumberFormat.getPercentInstance(Locale.US)
            Text(text = format.format(minOf(achievement.progress.value, achievement.progress.targetValue)))
        }
    )
}

private data class Achievement(
    val id: String,
    val name: String,
    val progress: Animatable<Float, AnimationVector1D>,
    val ball: BallMeta
)

internal data class BallMeta(
    val borderColors: List<Color>,
    val containerColors: List<Color>,
    val icon: ImageVector? = null,
    val iconRes: Int? = null
)

private class EnterOrchestrator(
    private val scope: CoroutineScope
) {
    private val channel = Channel<String>()
    val state = mutableStateMapOf<String, Animatable<Float, AnimationVector1D>>()

    init {
        scope.launch {
            for (id in channel) {
                val animatable = Animatable(0f)
                state[id] = animatable
                launch {
                    animatable.animateTo(1f, tween(300))
                }
                delay(500)
            }
        }
    }

    fun add(id: String) {
        scope.launch {
            channel.send(id)
        }
    }
}