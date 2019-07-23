package de.schalter.losungen.components.exceptions

class DataExceptionWrapper<T>(val value: T? = null, val error: Throwable? = null)