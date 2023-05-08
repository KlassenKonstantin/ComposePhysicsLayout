package de.apuri.physicslayout.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib.BodyConfig
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.physicsBody
import de.apuri.physicslayout.lib.simulation.rememberClock
import de.apuri.physicslayout.lib.simulation.rememberSimulation

val colors = listOf(
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Cyan,
    Color.Yellow,
)

val shapes = listOf(
    RectangleShape,
    CircleShape,
    RoundedCornerShape(64.dp),
    CutCornerShape(16.dp),
)

@Composable
fun SimpleScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var sliderDensity by remember { mutableStateOf(0.5f) }
        var sliderFriction by remember { mutableStateOf(0f) }
        var sliderRestitution by remember { mutableStateOf(0f) }
        var currentBorderIndex by remember { mutableStateOf(0) }

        val clock = rememberClock()
        val simulation = rememberSimulation(clock)

        Column(
            Modifier.systemBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                PhysicsLayout(
                    Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    shape = shapes[currentBorderIndex],
                    simulation = simulation
                ) {
                    GravitySensor { (x, y) ->
                        simulation.setGravity(Offset(-x, y).times(3f))
                    }
                    Row(
                        Modifier.fillMaxSize()
                    ) {
                        repeat(10) { col ->
                            Column(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                repeat(5) { row ->
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val bodyConfig = BodyConfig(
                                            density = sliderDensity,
                                            friction = sliderFriction,
                                            restitution = sliderRestitution,
                                        )
                                        Ball("$col$row", bodyConfig = bodyConfig)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Density",
                        Modifier
                            .padding(end = 16.dp)
                            .weight(1f)
                    )
                    Slider(value = sliderDensity, onValueChange = {
                        sliderDensity = it
                    }, Modifier.weight(3f), valueRange = 0f..1f)
                }
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Friction",
                        Modifier
                            .padding(end = 16.dp)
                            .weight(1f)
                    )
                    Slider(
                        value = sliderFriction,
                        onValueChange = { sliderFriction = it },
                        Modifier.weight(3f),
                        valueRange = 0f..1f
                    )
                }
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Restitution",
                        Modifier
                            .padding(end = 16.dp)
                            .weight(1f)
                    )
                    Slider(
                        value = sliderRestitution,
                        onValueChange = { sliderRestitution = it },
                        Modifier.weight(3f),
                        valueRange = 0f..1f
                    )
                }
                Row {
                    Button(onClick = { clock.pause() }) {
                        Text(text = "Pause")
                    }
                    Button(onClick = { clock.resume() }) {
                        Text(text = "Resume")
                    }
                    Button(onClick = { currentBorderIndex = (++currentBorderIndex).mod(shapes.size) }) {
                        Text(text = "Toggle border")
                    }
                }
            }
        }
    }
}

@Composable
fun Ball(
    id: String,
    color: Color = Color(0xFFF44336),
    bodyConfig: BodyConfig
) {
    Box(
        modifier = Modifier
            .physicsBody(id = id, shape = CircleShape, bodyConfig = bodyConfig, DragConfig())
            .size(32.dp)
            .background(color, CircleShape)
    )
}