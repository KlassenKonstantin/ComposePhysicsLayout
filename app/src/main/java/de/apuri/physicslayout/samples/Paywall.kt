package de.apuri.physicslayout.samples

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib2.LocalSimulation
import de.apuri.physicslayout.lib2.PhysicsLayout
import de.apuri.physicslayout.lib2.drag.DragConfig
import de.apuri.physicslayout.lib2.physicsBody
import de.apuri.physicslayout.lib2.rememberClock
import de.apuri.physicslayout.lib2.simulation.rememberSimulation
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

val color = Color(0xFF009688)

@Composable
fun Paywall() {
    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        color = color
    ) {
        val clock = rememberClock()
        val sim = rememberSimulation(clock)
        var currentGravity by remember { mutableStateOf(Offset(9.2f, 3.4f)) }
        val docked by remember {
            derivedStateOf {
                (currentGravity.x - 9.2f).absoluteValue < 0.2f
            }
        }

        LaunchedEffect(docked) {
            if (!docked) {
                Log.d("gaga", "run")
                clock.start()
            } else {
                delay(300)
                Log.d("gaga", "pause")
                clock.stop()
            }
        }

        Box {
            PhysicsLayout(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxHeight(0.7f).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Color.White),
                simulation = sim
            ) {
                val sim = LocalSimulation.current
                GravitySensor {(x, y, z) ->
                    currentGravity = Offset(y, z)
                    sim.setGravity(Offset(-x, y))
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.padding(32.dp)
                    ) {
                        Text(text = "Product Pro", style = MaterialTheme.typography.headlineMedium, modifier = Modifier
                            .physicsBody(dragConfig = DragConfig(), docked = docked)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    FeatureItem("Lorem ipsum", docked)
                    FeatureItem("Dolor", docked)
                    FeatureItem("Sit amet", docked)
                    FeatureItem("At vero eos et accusam", docked)
                    FeatureItem("Magna aliquyam erat", docked)

                    Spacer(modifier = Modifier.height(36.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Selector(text = "Free", price = "$0.00", selected = true, docked)
                        Selector(text = "Basic", price = "$9.99", selected = false, docked)
                        Selector(text = "Pro", price = "$19.99", selected = false, docked)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)) {
                        Button(
                            modifier = Modifier
                                .physicsBody(dragConfig = DragConfig(), docked = docked)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(text = "Purchase")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.Selector(text: String, price: String, selected: Boolean, docked: Boolean) {
    val borderColor = animateColorAsState(targetValue = if (selected) color else Color.LightGray)
    val borderStroke = BorderStroke(
        width = if (selected) 1.dp else Dp.Hairline,
        color = borderColor.value
    )
    OutlinedCard(
        modifier = Modifier
            .physicsBody(
                shape = RoundedCornerShape(12.dp),
                dragConfig = DragConfig(),
                docked = docked
            )
            .weight(1f),
        border = borderStroke,
        onClick = {}
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
            Text(text = "-", style = MaterialTheme.typography.bodySmall)
            Text(text = price, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun FeatureItem(text: String, docked: Boolean) {
    Row(
        Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
    ) {

        Box(
            Modifier.weight(1f)
        ) {
            Text(text = text, modifier = Modifier.physicsBody(dragConfig = DragConfig(), docked = docked), style = MaterialTheme.typography.bodyLarge)
        }
        Box(
            Modifier
                .physicsBody(shape = CircleShape, dragConfig = DragConfig(), docked = docked)
                .size(24.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = "", modifier = Modifier.padding(4.dp), tint = Color.White)
        }
    }
}