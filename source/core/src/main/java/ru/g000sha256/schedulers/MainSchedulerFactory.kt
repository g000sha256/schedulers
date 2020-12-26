package ru.g000sha256.schedulers

import io.reactivex.Scheduler

interface MainSchedulerFactory {

    fun createDeferredScheduler(): Scheduler

    fun createImmediateScheduler(): Scheduler

}