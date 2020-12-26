package ru.g000sha256.schedulers

import io.reactivex.Scheduler

interface SchedulersHolder {

    val computationScheduler: Scheduler
    val ioScheduler: Scheduler
    val mainDeferredScheduler: Scheduler
    val mainImmediateScheduler: Scheduler
    val newThreadScheduler: Scheduler
    val singleScheduler: Scheduler
    val trampolineScheduler: Scheduler
    val workScheduler: Scheduler

}