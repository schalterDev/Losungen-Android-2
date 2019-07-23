package de.schalter.losungen.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

object CoroutineUtils {

    @UseExperimental(ExperimentalCoroutinesApi::class)
    fun runSingleThread() {
        CoroutineDispatchers.Background = Dispatchers.Unconfined
        CoroutineDispatchers.Ui = Dispatchers.Unconfined
    }

}