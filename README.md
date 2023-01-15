# Physics Layout
![Maven Central](https://img.shields.io/maven-central/v/io.github.klassenkonstantin/physics-layout?style=flat-square&versionPrefix=0.3)

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
To get started, create a `PhysicsLayout` and fill it with Composables. Every root level Composable in `PhysicsLayout` must use the `body` modifier to tell the simulation how the Composable should behave in the physics world.

## PhysicsLayout
```kotlin
@Composable
fun PhysicsLayout(
    modifier: Modifier = Modifier,
    simulation: Simulation = rememberSimulation(),
    onBodiesAdded: OnBodiesAdded? = null,
    shape: Shape? = RectangleShape,
    content: @Composable PhysicsLayoutScope.() -> Unit,
)
```
- `simulation`: Does the mapping between layout and physics engine
- `onBodiesAdded`: Called when the layout bodies composed in this layout are ready to be used
- `shape`: The shape of the outer border of the `PhysicsLayout`

## body modifier
```kotlin
fun Modifier.body(
    id: String? = null,
    shape: Shape = RectangleShape,
    isStatic: Boolean = false,
    initialTranslation: DpOffset = DpOffset.Zero,
    dragConfig: DragConfig = DragConfig.NotDraggable
): Modifier
```
- `id`: The id the body should have in the simulation. Useful for operations that act directly on bodies (not yet supported).
- `shape`: Describes the outer bounds of the body. Supported shapes are:
  - [RectangleShape](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/package-summary#RectangleShape())
  - [CircleShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/package-summary#CircleShape())
  - [RoundedCornerShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/RoundedCornerShape)
  - [CutCornerShape](https://developer.android.com/reference/kotlin/androidx/compose/foundation/shape/CutCornerShape)
- `isStatic`: Set true for unmovable bodies like walls and floors.
- `initialTranslation`: Where this body should be placed in the layout. An `Offset` of (0,0) is the center of the layout, not top left.
- `dragConfig`: Set to `DragConfig.Draggable` to enable drag support

### Example usage
```kotlin
PhysicsLayout {
    Card(
        modifier = Modifier.body(
            shape = CircleShape,
        ),
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
```
This would add a ball with a star in the center of the layout, which then starts falling to the ground.

> Note: The `shape` must be set on both the body modifier and the `Card`.

### Change gravity
If you need to change the gravity of the simulated world, use `Simulation.setGravity`

## Caveats, notes, missing features
- I don't think Compose was made to display hundrets of Composables at the same time. So maybe it's not a good idea to build a particle system out of this.
- In general, what is true for all of Compose is especially true for this Layout: **Release builds perform way better than debug builds**.
- State is not restored on config changes 😱.
- Currently there is no way to observe bodies / collosions / etc.
