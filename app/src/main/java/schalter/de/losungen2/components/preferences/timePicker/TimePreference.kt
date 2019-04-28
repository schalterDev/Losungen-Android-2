package schalter.de.losungen2.components.preferences.timePicker

import android.content.Context
import android.content.res.TypedArray
import android.text.format.DateFormat
import android.util.AttributeSet
import androidx.preference.DialogPreference
import schalter.de.losungen2.R
import java.util.*


class TimePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private val calendar: Calendar = Calendar.getInstance()
    private var time = 60 * 7

    init {
        positiveButtonText = context.resources.getString(R.string.save)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }

    fun getHour() = time / 60
    fun getMinute() = time % 60

    fun setTime(hour: Int? = null, minute: Int? = null) {
        val newHour = hour ?: getHour()
        val newMinute = minute ?: getMinute()

        time = newMinute + 60 * newHour
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        time = a.getInt(index, 60 * 7)
        return time.toString()
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        time = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedInt(time)
            } else {
                getPersistedInt((defaultValue as String).toInt())
            }
        } else {
            if (defaultValue == null) {
                time
            } else {
                (defaultValue as String).toInt()
            }
        }
    }

    override fun getSummary(): CharSequence {
        val hours = getHour()
        val minutes = getMinute()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        return DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
    }

    fun persistValue() {
        persistInt(time)
        notifyChanged()
    }

}