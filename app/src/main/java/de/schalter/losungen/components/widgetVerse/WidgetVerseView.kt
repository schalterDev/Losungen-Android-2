package de.schalter.losungen.components.widgetVerse

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import de.schalter.losungen.R
import de.schalter.losungen.widgets.WidgetData


class WidgetVerseView : LinearLayout {

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
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        inflate(context, R.layout.app_widget_row, this)

        layoutParams = params
        textViewText = findViewById(R.id.textView_widget)
        textViewVerse = findViewById(R.id.textViewVerse_widget)
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    fun setText(text: String) {
        textViewText.text = text
    }

    fun setVerse(verse: String) {
        textViewVerse.text = verse
    }

    fun setStyle(widgetData: WidgetData) {
        textViewText.textSize = widgetData.fontSize.toFloat()
        textViewVerse.textSize = widgetData.fontSize.toFloat() - 2
        textViewText.setTextColor(widgetData.color)
        textViewVerse.setTextColor(widgetData.color)
    }
}
