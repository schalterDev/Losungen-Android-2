package de.schalter.losungen.widgets

import android.content.Context
import android.graphics.Color
import android.preference.PreferenceManager
import de.schalter.losungen.utils.PreferenceTags

/**
 * All widget ids are in TAG_WIDGET_IDS
 *
 * Configure the widget:
 * color: font color
 * background: background color
 * fontSize: font size in pixel
 * content: OT, NT or both
 *
 * The configuration is saved in sharedPreferences
 * TAG{widgetId} = data
 */
data class WidgetData(
        val widgetId: Int,
        var color: Int,
        var background: Int,
        var fontSize: Int,
        var content: WidgetContent,
        var contentToShow: String? = null) {

    fun save(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val ids = preferences.getStringSet(PreferenceTags.WIDGET_IDS, mutableSetOf())!!
        if (!ids.contains(widgetId.toString())) {
            ids.add(widgetId.toString())
        }

        val editor = preferences.edit()
        editor.putInt(PreferenceTags.WIDGET_COLOR + widgetId, color)
        editor.putInt(PreferenceTags.WIDGET_BACKGROUND + widgetId, background)
        editor.putInt(PreferenceTags.WIDGET_FONT_SIZE + widgetId, fontSize)
        editor.putString(PreferenceTags.WIDGET_CONTENT + widgetId, content.toString())
        editor.putStringSet(PreferenceTags.WIDGET_IDS, ids)
        editor.apply()
    }

    companion object {
        fun remove(context: Context, widgetId: Int) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            val ids = preferences.getStringSet(PreferenceTags.WIDGET_IDS, mutableSetOf())!!
            ids.remove(widgetId.toString())

            val editor = preferences.edit()
            editor.remove(PreferenceTags.WIDGET_COLOR + widgetId)
            editor.remove(PreferenceTags.WIDGET_BACKGROUND + widgetId)
            editor.remove(PreferenceTags.WIDGET_CONTENT + widgetId)
            editor.remove(PreferenceTags.WIDGET_FONT_SIZE + widgetId)
            editor.putStringSet(PreferenceTags.WIDGET_IDS, ids)
            editor.apply()
        }

        fun load(context: Context, widgetId: Int): WidgetData {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return WidgetData(
                    widgetId,
                    preferences.getInt(PreferenceTags.WIDGET_COLOR + widgetId, Color.BLACK),
                    preferences.getInt(PreferenceTags.WIDGET_BACKGROUND + widgetId, Color.WHITE),
                    preferences.getInt(PreferenceTags.WIDGET_FONT_SIZE + widgetId, 12),
                    WidgetContent.valueOf(preferences.getString(PreferenceTags.WIDGET_CONTENT + widgetId, WidgetContent.OLD_AND_NEW_TESTAMENT.toString())!!))
        }

        fun loadAll(context: Context): List<WidgetData> {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context);
            val ids = preferences.getStringSet(PreferenceTags.WIDGET_IDS, mutableSetOf())!!

            return ids.map { load(context, it.toInt()) }
        }

        fun generateWidgetId(): Int {
            return (Math.random() * Int.MAX_VALUE).toInt()
        }
    }
}

enum class WidgetContent {
    NEW_TESTAMENT,
    OLD_TESTAMENT,
    OLD_AND_NEW_TESTAMENT
}