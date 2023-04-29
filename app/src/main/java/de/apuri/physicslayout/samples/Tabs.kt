@file:OptIn(ExperimentalFoundationApi::class)

package de.apuri.physicslayout.samples

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.PhysicsLayoutScope
import de.apuri.physicslayout.lib.Simulation
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.rememberSimulation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun FlyingTabsScreen() {
    var shakeOffset by remember { mutableStateOf(DpOffset.Zero) }
    val simulation = rememberSimulation()
    Surface(modifier = Modifier
        .fillMaxSize()
        .offset {
            density.run {
                IntOffset(
                    shakeOffset.x
                        .toPx()
                        .toInt(),
                    shakeOffset.y
                        .toPx()
                        .toInt(),
                )
            }
        }, color = MaterialTheme.colorScheme.background) {
        val pagerState = rememberPagerState()
        val tabs = listOf(
            "One",
            "Two",
            "Three",
        )
        Column {
            Box(
                Modifier
                    .height(250.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF10B981))
            ) {
                Tabs(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    items = tabs,
                    pagerState = pagerState,
                    simulation = simulation,
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding(),
                state = pagerState,
                pageCount = tabs.size,
                beyondBoundsPageCount = 2
                ) {
                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    val scrollState = rememberScrollState()
                    val canScrollForward = scrollState.canScrollForward
                    LaunchedEffect(key1 = canScrollForward) {
                        if (!canScrollForward) {
                            //simulation.applyForce()
                            shakeOffset = DpOffset(0.dp, -20.dp)
                            delay(50)
                            shakeOffset = DpOffset.Zero
                        }
                    }
                    Log.d("bbb", "CAN $canScrollForward")
                    Column(
                        Modifier.verticalScroll(scrollState)
                    ) {
                        repeat(50) {
                            androidx.compose.material3.ListItem(headlineContent = { Text(text = "Lorem Ipsum $it") }, supportingContent = { Text(text = "Dolor") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    items: List<String>,
    pagerState: PagerState,
    simulation: Simulation
) {
    val scope = rememberCoroutineScope()
    GravitySensor { (x, y) ->
        simulation.setGravity(Offset(-x, y).times(3f))
    }
    PhysicsLayout(
        modifier = modifier,
        simulation = simulation,
    ) {
        val currentPage = pagerState.currentPage
        val currentPageOffset = pagerState.currentPageOffsetFraction

        items.forEachIndexed { index, label ->
            val state = when {
                currentPage == index -> TabState.Selected
                currentPage - index == 1 && currentPageOffset < 0 -> TabState.Approaching(currentPageOffset.absoluteValue)
                currentPage - index == -1 && currentPageOffset > 0 -> TabState.Approaching(currentPageOffset.absoluteValue)
                else -> TabState.Deselected
            }

            Tab(label = label, state) {
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        }
    }
}

@Composable
fun PhysicsLayoutScope.Tab(
    label: String,
    state: TabState,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .body(dragConfig = DragConfig.Draggable(), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .fillMaxWidth(0.329f),
        contentAlignment = Alignment.Center
    ) {
        val targetValue = when (state) {
            is TabState.Approaching -> state.value * 0.7f
            TabState.Deselected -> 0f
            TabState.Selected -> 1f
        }

        val animSpec = when (state) {
            is TabState.Approaching -> spring<Float>(stiffness = Spring.StiffnessHigh)
            TabState.Deselected -> spring<Float>(stiffness = Spring.StiffnessHigh)
            TabState.Selected -> spring<Float>(stiffness = 6_000f, dampingRatio = 0.3f)
        }

        if (label == "One") {
            Log.d("asdf", targetValue.toString())
        }

        val progress by animateFloatAsState(
            targetValue = targetValue,
            animationSpec = animSpec
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.12f), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = -size.height / 3f * progress
                        alpha = 0.5f + 0.5f * (1 - progress)
                    }
                    .padding(vertical = 12.dp),
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = size.height + 1 - size.height * progress
                    val overshoot = java.lang.Float.max(0f, progress - 1f)
                    scaleY = java.lang.Float.max(1f, 1f + overshoot * 2f)
                }
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF10B981)
            )
        }
    }
}

sealed interface TabState {
    object Selected: TabState
    object Deselected: TabState
    data class Approaching(val value: Float): TabState
}