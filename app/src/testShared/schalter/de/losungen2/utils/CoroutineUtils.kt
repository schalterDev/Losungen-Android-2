package schalter.de.losungen2.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

object CoroutineUtils {

    @UseExperimental(ExperimentalCoroutinesApi::class)
    fun runSingleThread() {
        CoroutineDispatchers.Background = Dispatchers.Unconfined
        CoroutineDispatchers.Ui = Dispatchers.Unconfined
    }

}