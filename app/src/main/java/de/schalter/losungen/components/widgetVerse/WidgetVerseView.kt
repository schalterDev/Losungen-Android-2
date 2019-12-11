package de.schalter.losungen.components.widgetVerse

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import de.schalter.losungen.R
import de.schalter.losungen.widgets.WidgetData


class WidgetVerseView : FrameLayout {

    var textViewText: TextView
    var textViewVerse: TextView

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initAttributes(attributeSet)
    }

    init {
        val view = View.inflate(context, R.layout.app_widget_row, null)

        textViewText = view.findViewById(R.id.textView_widget)
        textViewVerse = view.findViewById(R.id.textViewVerse_widget)

        addView(view)
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    fun setText(text: String) {
        textViewText.text = text
    }

    fun setVerse(verse: String) {
        textViewVerse.text = verse
    }

    fun setStyle(widgetData: WidgetData, setBackground: Boolean = true) {
        textViewText.textSize = widgetData.fontSize.toFloat()
        textViewVerse.textSize = widgetData.fontSize.toFloat() - 2
        textViewText.setTextColor(widgetData.color)
        textViewVerse.setTextColor(widgetData.color)

        if (setBackground) {
            textViewText.setBackgroundColor(widgetData.background)
            textViewVerse.setBackgroundColor(widgetData.background)
        }
    }
}
