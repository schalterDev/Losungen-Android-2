package de.schalter.losungen.utils

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    observeForever { t ->
        value = t
        latch.countDown()
    }

    latch.await(2, TimeUnit.SECONDS)
    return value
}
