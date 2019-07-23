package de.schalter.losungen2.components.exceptions

import android.content.Context

class TranslatableException(private val stringId: Int) : Exception() {
    fun getStringForUser(context: Context): String {
        return context.getString(stringId)
    }
}