package ru.g000sha256.scheduler

import android.os.Handler
import android.os.Looper
import io.reactivex.Scheduler
import ru.g000sha256.schedulers.MainSchedulerFactory

class MainSchedulerFactoryImpl(private val isAsynchronous: Boolean = true) : MainSchedulerFactory {

    override fun createDeferredScheduler(): Scheduler {
        return createScheduler(isImmediate = false)
    }

    override fun createImmediateScheduler(): Scheduler {
        return createScheduler(isImmediate = true)
    }

    private fun createScheduler(isImmediate: Boolean): Scheduler {
        val looper = Looper.getMainLooper()
        val handler = Handler(looper)
        return HandlerScheduler(isAsynchronous, isImmediate, handler)
    }

}