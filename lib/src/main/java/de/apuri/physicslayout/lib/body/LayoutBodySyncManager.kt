package de.apuri.physicslayout.lib.body

internal class LayoutBodySyncManager {
    private val cachedLayoutBodies = mutableListOf<LayoutBody>()

    internal fun syncBodies(currentBodies: List<LayoutBody>): SyncResult {
        val removedBodies = cachedLayoutBodies.filter { oldBody -> currentBodies.none { it.id == oldBody.id } }
        val addedBodies = currentBodies.filter { currentBody -> cachedLayoutBodies.none { it.id == currentBody.id } }
        val updatedBodies = currentBodies.filter { currentBody ->
            cachedLayoutBodies.firstOrNull { cachedBody ->
                currentBody.id == cachedBody.id
            } != currentBody
        }

        cachedLayoutBodies.clear()
        cachedLayoutBodies += currentBodies

        return SyncResult(
            added = addedBodies,
            removed = removedBodies,
            updated = updatedBodies
        )
    }

    internal data class SyncResult(
        val added: List<LayoutBody>,
        val removed: List<LayoutBody>,
        val updated: List<LayoutBody>,
    )
}