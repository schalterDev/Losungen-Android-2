package de.schalter.losungen.components.widgetVerse

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import de.schalter.losungen.R
import de.schalter.losungen.utils.Wallpaper
import de.schalter.losungen.widgets.WidgetData


class WidgetPreview : LinearLayout {

    private val MAX_HEIGHT = 500

    private var container: LinearLayout
    private var verse1: WidgetVerseView
    private var verse2: WidgetVerseView

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initAttributes(attributeSet)
    }

    init {
        val view = inflate(context, R.layout.app_widget_preview, null)

        view.findViewById<ScrollView>(R.id.widget_preview_background).apply {
            this.background = Wallpaper.getWallpaperDrawable(context)
        }
        container = view.findViewById(R.id.widget_preview_container)
        verse1 = view.findViewById(R.id.widget_preview_verse1)
        verse2 = view.findViewById(R.id.widget_preview_verse2)

        addView(view)
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeightMeasureSpec = heightMeasureSpec

        try {
            var heightSize = MeasureSpec.getSize(newHeightMeasureSpec)
            if (heightSize > MAX_HEIGHT) {
                heightSize = MAX_HEIGHT
                newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST)
                layoutParams.height = heightSize
            }
        } catch (e: Exception) {
        } finally {
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
        }
    }

    fun setWidgetData(widgetData: WidgetData) {
        container.setBackgroundColor(widgetData.background)

        widgetData.content.getOrNull(0)?.let {
            verse1.visibility = View.VISIBLE
            verse1.setText(it.verseText)
            verse1.setVerse(it.verseVerse)
            verse1.setStyle(widgetData)
        } ?: run { verse1.visibility = View.GONE }

        widgetData.content.getOrNull(1)?.let {
            verse2.visibility = View.VISIBLE
            verse2.setText(it.verseText)
            verse2.setVerse(it.verseVerse)
            verse2.setStyle(widgetData)
        } ?: run { verse2.visibility = View.GONE }
    }
}
