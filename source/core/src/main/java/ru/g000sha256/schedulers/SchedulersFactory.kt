package ru.g000sha256.schedulers

import io.reactivex.Scheduler
import java.util.concurrent.Executor
import java.util.concurrent.ThreadFactory

interface SchedulersFactory {

    fun createExecutorScheduler(executor: Executor): Scheduler

    fun createExecutorScheduler(executor: Executor, interruptibleWorker: Boolean): Scheduler

    fun createOneThreadScheduler(): Scheduler

    fun createOneThreadScheduler(threadFactory: ThreadFactory): Scheduler

}