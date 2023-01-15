package de.apuri.physicslayout.lib.border

import androidx.compose.ui.graphics.Shape

internal class LayoutShapeSyncManager {
    private var layoutShape: LayoutShape? = null

    fun updateLayoutShape(
        layoutWidth: Int,
        layoutHeight: Int,
        shape: Shape?
    ) : LayoutShape? {
        val newLayoutShape = if (shape == null) null else LayoutShape(layoutWidth, layoutHeight, shape)
        val changed = newLayoutShape != layoutShape
        if (changed) {
            layoutShape = newLayoutShape
        }
        return if (changed) layoutShape else null
    }
}