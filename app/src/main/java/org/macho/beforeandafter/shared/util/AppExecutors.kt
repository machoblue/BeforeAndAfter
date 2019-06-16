package org.macho.beforeandafter.shared.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors {
    val diskIO: Executor
    val mainThread: Executor

    init {
        diskIO = DiskIOThreadExecutor()
        mainThread = MainThreadExecutor()
    }

    private class MainThreadExecutor: Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            mainThreadHandler.post(command)
        }
    }

    private class DiskIOThreadExecutor: Executor {
        private val diskIO: Executor

        init {
            diskIO = Executors.newSingleThreadExecutor()
        }

        override fun execute(command: Runnable?) {
            diskIO.execute(command)
        }
    }
}