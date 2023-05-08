package de.apuri.physicslayout.lib.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class Clock internal constructor(
    private val scope: CoroutineScope,
    autoStart: Boolean
) {

    internal val frames = MutableSharedFlow<Double>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    private var job: Job? = null

    init {
        if (autoStart) resume()
    }

    fun resume() {
        if (job != null) return

        job = scope.launch {
            var last = System.nanoTime()
            while (true) {
                val now = System.nanoTime()
                val elapsed = (now - last).toDouble() / 1.0e9
                last = now
                frames.tryEmit(elapsed)
                delay(1)
            }
        }
    }

    fun pause() {
        job?.cancel()
        job = null
    }
}

@Composable
fun rememberClock(
    autoStart: Boolean = true
): Clock {
    val scope = rememberCoroutineScope()
    return remember {
        Clock(scope, autoStart)
    }
}