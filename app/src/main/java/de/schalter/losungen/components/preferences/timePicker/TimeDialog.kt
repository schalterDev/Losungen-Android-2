package de.schalter.losungen.components.preferences.timePicker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import de.schalter.losungen.utils.LanguageUtils


class TimeDialog : PreferenceDialogFragmentCompat() {

    private var timePicker: TimePicker? = null

    override fun onCreateDialogView(context: Context): View {
        timePicker = TimePicker(context)
        return timePicker!!
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        timePicker!!.setIs24HourView(LanguageUtils.is24Hour(context!!))
        val pref = preference as TimePreference
        timePicker!!.currentHour = pref.getHour()
        timePicker!!.currentMinute = pref.getMinute()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as TimePreference
            pref.setTime(timePicker!!.currentHour, timePicker!!.currentMinute)

            pref.persistValue()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key: String) = TimeDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, key)
            }
        }
    }
}