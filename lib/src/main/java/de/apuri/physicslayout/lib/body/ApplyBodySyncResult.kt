package de.apuri.physicslayout.lib.body

import de.apuri.physicslayout.lib.WorldBody
import org.dyn4j.geometry.MassType

internal fun interface ApplyBodySyncResult {
    operator fun invoke(
        added: List<WorldBody>,
        removed: List<WorldBody>,
        updated: List<WorldBody>,
    )
}

internal class DefaultApplyBodySyncResult(
    private val bodyManager: BodyManager,
) : ApplyBodySyncResult {
    override fun invoke(
        added: List<WorldBody>,
        removed: List<WorldBody>,
        updated: List<WorldBody>,
    ) {
        removed.forEach {
            bodyManager.removeBody(it.id)
        }

        added.forEach { worldBody ->
            createBody(worldBody).also { newBody ->
                newBody.translate(worldBody.initialTranslation)
                bodyManager.addBody(worldBody.id, newBody)
            }
        }

        updated.forEach { worldBody ->
            bodyManager.bodies[worldBody.id]?.let {
                updateBody(it, worldBody)
            }
        }
    }

    private fun createBody(
        worldBody: WorldBody
    ) = Body(
        worldBody.width,
        worldBody.height
    ).apply {
        angularDamping = 0.7
        isAtRestDetectionEnabled = false
        applyFixtures(worldBody)
        setMass(if (worldBody.isStatic) MassType.INFINITE else MassType.NORMAL)
    }

    private fun updateBody(
        body: Body,
        newWorldBody: WorldBody
    ) = body.apply {
        updateSize(newWorldBody.width, newWorldBody.height)
        applyFixtures(newWorldBody)
        setMass(if (newWorldBody.isStatic) MassType.INFINITE else MassType.NORMAL)
    }

    private fun Body.applyFixtures(worldBody: WorldBody) {
        removeAllFixtures()
        createFixtures(worldBody.shape).forEach {
            addFixture(it, 1.0, 0.2, 0.4)
        }
    }
}