package schalter.de.losungen2.components.exceptions

class DataExceptionWrapper<T>(val value: T? = null, val error: Throwable? = null)