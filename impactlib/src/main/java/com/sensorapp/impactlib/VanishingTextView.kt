package com.sensorapp.impactlib

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class VanishingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var future: ScheduledFuture<*>? = null
    private var runnable: Runnable? = null

    init {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    fun show(delayInMs: Long) {
        visibility = VISIBLE

        if (runnable == null) {
            runnable = Runnable {
                visibility = INVISIBLE
            }
        }

        future?.cancel(true)
        future = scheduledExecutorService?.schedule(runnable, delayInMs, TimeUnit.MILLISECONDS)
    }

    fun cancel() {
        future?.cancel(true)
        scheduledExecutorService?.shutdown()
    }
}