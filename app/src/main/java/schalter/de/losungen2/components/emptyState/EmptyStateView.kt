package schalter.de.losungen2.components.emptyState

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import schalter.de.losungen2.R

class EmptyStateView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        val view = View.inflate(context, R.layout.empty_words, null)
        addView(view)
    }
}
