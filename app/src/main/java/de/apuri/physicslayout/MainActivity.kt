package de.apuri.physicslayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.apuri.physicslayout.samples.ShapesScreen
import de.apuri.physicslayout.samples.StarLauncherScreen
import de.apuri.physicslayout.ui.theme.PhysicsLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            PhysicsLayoutTheme {
                val navController = rememberNavController()
                Box(
                    Modifier.fillMaxSize()
                ) {
                    NavHost(
                        modifier = Modifier,
                        navController = navController,
                        startDestination = "samplePicker"
                    ) {
                        composable("samplePicker") { SamplePicker { navController.navigate(it) } }
                        composable("Star Launcher") { StarLauncherScreen() }
                        composable("Shapes") { ShapesScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun SamplePicker(onSamplePicked: (String) -> Unit) {
    val samples = listOf(
        "Star Launcher",
        "Shapes"
    )

    LazyColumn(
        modifier = Modifier.systemBarsPadding()
    ) {
        items(samples) {
            SampleItem(it) { onSamplePicked(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleItem(id: String, onSampleItemClicked: () -> Unit) {
    ListItem(
        headlineText = { Text(id) },
        modifier = Modifier.clickable {
            onSampleItemClicked()
        }
    )
}