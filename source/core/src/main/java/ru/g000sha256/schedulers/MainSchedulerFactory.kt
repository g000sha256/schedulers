package ru.g000sha256.schedulers

import io.reactivex.rxjava3.core.Scheduler

interface MainSchedulerFactory {

    fun createDeferredScheduler(): Scheduler

    fun createImmediateScheduler(): Scheduler

}