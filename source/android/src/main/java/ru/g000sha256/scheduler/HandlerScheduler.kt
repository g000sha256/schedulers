package ru.g000sha256.scheduler

import android.os.Handler
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.TimeUnit

internal class HandlerScheduler(
    private val isAsynchronous: Boolean,
    private val isImmediate: Boolean,
    private val handler: Handler
) : Scheduler() {

    override fun createWorker(): Worker {
        return HandlerWorker(isAsynchronous, isImmediate, handler)
    }

    override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val worker = createWorker()
        val runnable = RxJavaPlugins.onSchedule(run)
        return worker.schedule(runnable, delay, unit)
    }

}