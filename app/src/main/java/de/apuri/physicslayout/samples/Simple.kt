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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.lib2.PhysicsLayout

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
    RoundedCornerShape(32.dp),
    CutCornerShape(16.dp),
)

@Composable
fun SimpleScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Box(
                modifier = Modifier
                    .systemBarsPadding()
                    .aspectRatio(1f)
            ) {
                PhysicsLayout(
                    Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    shape = RectangleShape
                ) {
                    GravitySensor {
                        //sim.setGravity(it.copy(x = -it.x).times(3f))
                    }
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(1f),
//                    contentAlignment = Alignment.Center
//                ) {
//
//                }

                    Row(
                        Modifier.fillMaxSize()
                    ) {
                        repeat(2) { col ->
                            Column(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                repeat(2) { row ->
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Ball("$col$row")
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
fun Ball(id: String, color: Color = Color(0xFFF44336)) {
    Box(
        modifier = Modifier
//            .physicsBody(id, null)
            .size(32.dp)
            .background(color, CircleShape)
    )
}

//                                        Color.hsv(
//                                            (((col + 1) * (row + 1)) / (7f * 7f)) * 360,
//                                            1f,
//                                            1f
//                                        )