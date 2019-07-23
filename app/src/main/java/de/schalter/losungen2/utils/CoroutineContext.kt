package de.schalter.losungen2.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoroutineDispatchers {
    var Background: CoroutineDispatcher = Dispatchers.Default
    var Ui: CoroutineDispatcher = Dispatchers.Main
}