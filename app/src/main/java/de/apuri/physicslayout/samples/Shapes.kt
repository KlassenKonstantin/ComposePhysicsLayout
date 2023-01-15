package de.apuri.physicslayout.samples

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.GravitySensor
import de.apuri.physicslayout.R
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.PhysicsLayoutScope
import de.apuri.physicslayout.lib.rememberSimulation

@Composable
fun ShapesScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var gravity by remember { mutableStateOf(Offset.Zero) }
        GravitySensor {
            gravity = it.copy(x = -it.x).times(3f)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {

            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    PhysicsInstance({ gravity }, RectangleShape)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    PhysicsInstance({ gravity }, CircleShape)
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    PhysicsInstance({ gravity }, RoundedCornerShape(24.dp))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    PhysicsInstance({ gravity }, CutCornerShape(20))
                }
            }
        }
    }
}

@Composable
fun PhysicsInstance(
    provideGravity: () -> Offset,
    shape: Shape
) {
    val simulation = rememberSimulation()
    simulation.setGravity(provideGravity())
    PhysicsLayout(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape)
            .clip(shape),
        simulation = simulation,
        shape = shape
    ) {
        Ball(
            shape = CircleShape, ball = BallMeta(
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
        )
        Ball(
            shape = RectangleShape, ball = BallMeta(
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
        )

        Ball(
            shape = CutCornerShape(33), ball = BallMeta(
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
        )
        Ball(
            shape = RoundedCornerShape(25), ball = BallMeta(
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
        )
    }
}

@Composable
private fun PhysicsLayoutScope.Ball(shape: Shape, ball: BallMeta) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Brush.verticalGradient(ball.containerColors), shape)
            .border(BorderStroke(2.dp, Brush.verticalGradient(ball.borderColors)), shape)
            .body(
                shape = shape,
                dragConfig = DragConfig.Draggable(
                    maxForce = 75.0
                ),
            ),
        contentAlignment = Alignment.Center
    ) {
        if (ball.icon != null) {
            Icon(modifier = Modifier.scale(0.7f), imageVector = ball.icon, contentDescription = "", tint = Color.White)
        } else if (ball.iconRes != null) {
            Icon(modifier = Modifier.scale(0.7f), painter = painterResource(id = ball.iconRes), contentDescription = "", tint = Color.White)
        }
    }
}

internal data class BallMeta(
    val borderColors: List<Color>,
    val containerColors: List<Color>,
    val icon: ImageVector? = null,
    val iconRes: Int? = null
)