# Physics Layout
![Maven Central](https://img.shields.io/maven-central/v/io.github.klassenkonstantin/physics-layout?style=flat-square&versionPrefix=0.4)

This library offers a [dyn4j](https://www.dyn4j.org) wrapper for [Jetpack Compose](https://developer.android.com/jetpack/compose).

## 🚧 Experimental 🚧
Before reaching version 1.0, this library is considered experimental, which means that there is no guaranteed backwards compatibility between versions. Signatures, interfaces, names, etc. may and will most likely change.

## Sample App
https://user-images.githubusercontent.com/1836066/206856910-d2172e7e-64da-454e-99b9-8171cf5f5eeb.mov

## Download
```
dependencies {
    implementation 'io.github.klassenkonstantin:physics-layout:<version>'
}
```

# How to use
To get started, create a `PhysicsLayout` and add arbitrary content to it. Add the `physicsBody` modifier to Composables that should be part of the physics simulation.

## PhysicsLayout
```kotlin
@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    shape: Shape? = RectangleShape,
    scale: Dp = DEFAULT_SCALE,
    simulation: Simulation = rememberSimulation(),
    content: @Composable BoxScope.() -> Unit
)
```
- `shape`: The shape of the outer border of the `PhysicsLayout`
- `scale`: How many Dps should be considered one meter. Bodies shouldn't be smaller than one meter
- `simulation`: Does the mapping between layout and physics engine
- `content`: The arbitrary layout

## physicsBody modifier
```kotlin
fun Modifier.physicsBody(
    id: String? = null,
    shape: Shape = RectangleShape,
    bodyConfig: BodyConfig = BodyConfig(),
    dragConfig: DragConfig? = null,
)
```
- `id`: The id the body should have in the simulation. Useful for operations that act directly on bodies (not yet supported).
- `shape`: Describes the outer bounds of the body. Supported shapes are:
  - [RectangleShape](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/package-summary#RectangleShape())
  - [CircleShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/package-summary#CircleShape())
  - [RoundedCornerShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/RoundedCornerShape)
  - [CutCornerShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/CutCornerShape)
- `bodyConfig`: Configures properties of the body
- `dragConfig`: Set a `DragConfig` to enable drag support, or `null` to disable dragging

## Clock
By default `Simulation` uses a default `Clock` which automatically starts running. To pause and resume a `Clock`, create an instance with `rememberClock()` and pass that to the `Simulation`.

### Example usage
```kotlin
@Composable
fun SimpleScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PhysicsLayout {
            Card(
                modifier = Modifier.physicsBody(
                    shape = CircleShape,
                ).align(Alignment.Center),
                shape = CircleShape,
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = Color.White
                )
            }
        }
    }
}
```
This example adds a ball with a star in the center of the layout, which then starts falling to the ground.

> Note: The `shape` must be set on both the body modifier and the `Card`.

### Change gravity
If you need to change the gravity of the simulated world, use `Simulation.setGravity`

## Caveats, notes, missing features
- I don't think Compose was made to display hundrets of Composables at the same time. So maybe it's not a good idea to build a particle system out of this.
- In general, what is true for all of Compose is especially true for this Layout: **Release builds perform way better than debug builds**.
- State is not restored on config changes 😱.
- Currently there is no way to observe bodies / collosions / etc.
- Not tested with scrolling containers
