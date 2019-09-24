package de.schalter.losungen.utils

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

object AsyncTestUtils {

    @UseExperimental(ExperimentalCoroutinesApi::class)
    fun runSingleThread() {
        CoroutineDispatchers.Background = Dispatchers.Unconfined
        CoroutineDispatchers.Ui = Dispatchers.Unconfined

        // override Schedulers.io()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        // override AndroidSchedulers.mainThread()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

}