package ru.g000sha256.scheduler

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

internal class HandlerWorker(
    private val isAsynchronous: Boolean,
    private val isImmediate: Boolean,
    private val handler: Handler
) : Scheduler.Worker() {

    @Volatile
    private var isDisposed = false

    override fun dispose() {
        handler.removeCallbacksAndMessages(this)
        isDisposed = true
    }

    override fun isDisposed(): Boolean {
        return isDisposed
    }

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        if (isDisposed) return this
        val runnable = HandlerRunnable(run)
        val isMainThread = Looper.myLooper() === Looper.getMainLooper()
        if (isMainThread && isImmediate && delay == 0L) {
            if (isDisposed) return this
            runnable.run()
        } else {
            if (isDisposed) return this
            val message = Message.obtain(handler, runnable)
            message.obj = this
            if (isAsynchronous && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                message.isAsynchronous = true
            }
            val delayMillis = unit.toMillis(delay)
            if (isDisposed) return this
            handler.sendMessageDelayed(message, delayMillis)
            if (isDisposed) handler.removeCallbacks(runnable)
        }
        return this
    }

}