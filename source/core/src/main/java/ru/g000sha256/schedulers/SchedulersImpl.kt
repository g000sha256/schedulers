package ru.g000sha256.schedulers

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.internal.schedulers.ComputationScheduler
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import io.reactivex.rxjava3.internal.schedulers.IoScheduler
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler
import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory
import io.reactivex.rxjava3.internal.schedulers.SingleScheduler
import io.reactivex.rxjava3.internal.schedulers.TrampolineScheduler
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.math.max
import kotlin.math.min

private const val MIN_THREAD_COUNT = 2

class SchedulersImpl(
    mainSchedulerFactory: MainSchedulerFactory,
    computationThreadFactory: ThreadFactory? = null,
    ioThreadFactory: ThreadFactory? = null,
    newThreadThreadFactory: ThreadFactory? = null,
    singleThreadFactory: ThreadFactory? = null,
    workThreadFactory: ThreadFactory? = null
) : Schedulers {

    override val computationScheduler by lazy { initComputationScheduler(computationThreadFactory) }
    override val ioScheduler by lazy { initIoScheduler(ioThreadFactory) }
    override val mainDeferredScheduler by lazy { mainSchedulerFactory.createDeferredScheduler() }
    override val mainImmediateScheduler by lazy { mainSchedulerFactory.createImmediateScheduler() }
    override val newThreadScheduler by lazy { initNewThreadScheduler(newThreadThreadFactory) }
    override val singleScheduler by lazy { initSingleScheduler(singleThreadFactory) }
    override val trampolineScheduler by lazy { initTrampolineScheduler() }
    override val workScheduler by lazy { initWorkScheduler(workThreadFactory) }

    override fun createExecutorScheduler(executor: Executor): Scheduler {
        return createExecutorScheduler(executor, false)
    }

    override fun createExecutorScheduler(executor: Executor, interruptibleWorker: Boolean): Scheduler {
        return createExecutorScheduler(executor, interruptibleWorker, false)
    }

    override fun createExecutorScheduler(executor: Executor, interruptibleWorker: Boolean, fair: Boolean): Scheduler {
        return ExecutorScheduler(executor, interruptibleWorker, fair)
    }

    override fun createOneThreadScheduler(): Scheduler {
        val threadFactory = createThreadFactory("RxOneThreadScheduler")
        return createOneThreadScheduler(threadFactory)
    }

    override fun createOneThreadScheduler(threadFactory: ThreadFactory): Scheduler {
        val executor = Executors.newSingleThreadExecutor(threadFactory)
        return createExecutorScheduler(executor)
    }

    private fun createThreadFactory(prefix: String): ThreadFactory {
        return RxThreadFactory(prefix, Thread.NORM_PRIORITY, false)
    }

    private fun createThreadFactory(prefix: String, key: String, nonBlocking: Boolean): ThreadFactory {
        var priority = Integer.getInteger(key, Thread.NORM_PRIORITY)
        priority = min(Thread.MAX_PRIORITY, priority)
        priority = max(Thread.MIN_PRIORITY, priority)
        return RxThreadFactory(prefix, priority, nonBlocking)
    }

    private fun initComputationScheduler(threadFactory: ThreadFactory?): Scheduler {
        val threadFactory = threadFactory ?: createThreadFactory("RxComputationScheduler", "rx3.computation-priority", true)
        return ComputationScheduler(threadFactory)
    }

    private fun initIoScheduler(threadFactory: ThreadFactory?): Scheduler {
        val threadFactory = threadFactory ?: createThreadFactory("RxIoScheduler", "rx3.io-priority", false)
        return IoScheduler(threadFactory)
    }

    private fun initNewThreadScheduler(threadFactory: ThreadFactory?): Scheduler {
        val threadFactory = threadFactory ?: createThreadFactory("RxNewThreadScheduler", "rx3.newthread-priority", false)
        return NewThreadScheduler(threadFactory)
    }

    private fun initSingleScheduler(threadFactory: ThreadFactory?): Scheduler {
        val threadFactory = threadFactory ?: createThreadFactory("RxSingleScheduler", "rx3.single-priority", true)
        return SingleScheduler(threadFactory)
    }

    private fun initTrampolineScheduler(): Scheduler {
        return TrampolineScheduler.instance()
    }

    private fun initWorkScheduler(threadFactory: ThreadFactory?): Scheduler {
        val runtime = Runtime.getRuntime()
        val availableProcessors = runtime.availableProcessors()
        val threadCount = max(MIN_THREAD_COUNT, availableProcessors)
        val threadFactory = threadFactory ?: createThreadFactory("RxWorkScheduler")
        val executor = Executors.newFixedThreadPool(threadCount, threadFactory)
        return createExecutorScheduler(executor)
    }

}