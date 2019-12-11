package de.schalter.losungen.components.widgetVerse

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import de.schalter.losungen.R
import de.schalter.losungen.utils.Wallpaper
import de.schalter.losungen.widgets.WidgetContentType
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
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        inflate(context, R.layout.app_widget_preview, this)

        layoutParams = params

        findViewById<ScrollView>(R.id.widget_preview_background).apply {
            this.background = Wallpaper.getWallpaperDrawable(context)
        }
        container = findViewById(R.id.widget_preview_container)
        verse1 = findViewById(R.id.widget_preview_verse1)
        verse2 = findViewById(R.id.widget_preview_verse2)
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MAX_HEIGHT, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }

    fun setWidgetData(widgetData: WidgetData) {
        container.setBackgroundColor(widgetData.background)

        if (widgetData.contentType.size == 2) {
            setVerse1(widgetData)
            setVerse2(widgetData)
        } else if (widgetData.contentType.size == 1) {
            if (widgetData.content.size == 1) {
                setVerse1(widgetData)
                verse2.visibility = View.GONE
            } else {
                // first entry in content contains OT, second NT
                if (widgetData.contentType.contains(WidgetContentType.OLD_TESTAMENT)) {
                    setVerse1(widgetData)
                    verse2.visibility = View.GONE
                } else {
                    setVerse2(widgetData)
                    verse1.visibility = View.GONE
                }
            }
        }
    }

    private fun setVerse1(widgetData: WidgetData) {
        widgetData.content.getOrNull(0)?.let {
            verse1.visibility = View.VISIBLE
            verse1.setText(it.verseText)
            verse1.setVerse(it.verseVerse)
            verse1.setStyle(widgetData)
        } ?: run { verse1.visibility = View.GONE }
    }

    private fun setVerse2(widgetData: WidgetData) {
        widgetData.content.getOrNull(1)?.let {
            verse2.visibility = View.VISIBLE
            verse2.setText(it.verseText)
            verse2.setVerse(it.verseVerse)
            verse2.setStyle(widgetData)
        } ?: run { verse2.visibility = View.GONE }
    }
}
