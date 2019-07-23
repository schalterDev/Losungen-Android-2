package de.schalter.losungen.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoroutineDispatchers {
    var Background: CoroutineDispatcher = Dispatchers.Default
    var Ui: CoroutineDispatcher = Dispatchers.Main
}