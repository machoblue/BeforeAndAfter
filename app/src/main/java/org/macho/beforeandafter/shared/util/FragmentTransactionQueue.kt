package org.macho.beforeandafter.shared.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class FragmentTransactionQueue: LifecycleObserver {

    private var isPaused = true
    private val taskQueue: MutableList<() -> Unit> = mutableListOf()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        isPaused = false

        var task = taskQueue.firstOrNull()
        while (task != null) {
            task()
            task = taskQueue.firstOrNull()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        isPaused = true
    }

    fun post(task: () -> Unit) {
        if (isPaused) {
            taskQueue.add(task)

        } else {
            task()
        }
    }
}