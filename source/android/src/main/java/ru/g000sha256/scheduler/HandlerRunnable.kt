package ru.g000sha256.scheduler

import io.reactivex.plugins.RxJavaPlugins

internal class HandlerRunnable(private val runnable: Runnable) : Runnable {

    override fun run() {
        try {
            runnable.run()
        } catch (throwable: Throwable) {
            RxJavaPlugins.onError(throwable)
        }
    }

}