package de.apuri.physicslayout.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib2.LocalSimulation
import de.apuri.physicslayout.lib2.PhysicsLayout
import de.apuri.physicslayout.lib2.physicsBody

val colors = listOf(
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Cyan,
    Color.Yellow,
)

@Composable
fun SimpleScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PhysicsLayout(
            Modifier.systemBarsPadding().wrapContentSize(Alignment.TopCenter)
        ) {
            val sim = LocalSimulation.current
            GravitySensor {
                sim.setGravity(it.copy(x = -it.x).times(3f))
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.background(Color.DarkGray)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        repeat(7) { col ->
                            Column(
                                Modifier
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                repeat(6) { row ->
                                    Box(
                                        Modifier.aspectRatio(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Ball(
                                            "$col$row",
                                            Color.hsv(
                                                (((col + 1) * (row + 1)) / (7f * 6f)) * 360,
                                                1f,
                                                1f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Row(
            Modifier
                .systemBarsPadding()
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width((1f / LocalDensity.current.density).dp)
                    .background(color = DividerDefaults.color)
            )
        }

        Column(
            Modifier
                .systemBarsPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Divider(thickness = (1f / LocalDensity.current.density).dp)
        }
    }
}

@Composable
fun Ball(id: String, color: Color = Color(0xFF065f46)) {
    Box(
        modifier = Modifier
            .physicsBody(id, CircleShape)
            .size(32.dp)
            .background(color, CircleShape)
    )
}