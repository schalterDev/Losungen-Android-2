package schalter.de.losungen2.components.emptyState

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import schalter.de.losungen2.R


class EmptyStateView : FrameLayout {

    var button: Button
    var title: TextView
    var icon: ImageView

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initAttributes(attributeSet)
    }

    init {
        val view = View.inflate(context, R.layout.empty_state, null)

        button = view.findViewById(R.id.emptyStateImport)
        title = view.findViewById(R.id.emptyStateTitle)
        icon = view.findViewById(R.id.emptyStateIcon)

        addView(view)
    }

    private fun initAttributes(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EmptyStateView)

        try {
            val titleRes = typedArray.getResourceId(R.styleable.EmptyStateView_emptyStateTitle, -1)
            val buttonRes = typedArray.getResourceId(R.styleable.EmptyStateView_emptyStateButtonText, -1)
            val iconRes = typedArray.getResourceId(R.styleable.EmptyStateView_emptyStateIcon, -1)

            if (titleRes != -1) setTitle(titleRes)
            if (buttonRes != -1) setButtonText(buttonRes)
            if (iconRes != -1) setIcon(iconRes)
        } finally {
            typedArray.recycle()
        }
    }

    fun setTitle(titleRes: Int) {
        title.text = resources.getText(titleRes)
    }

    fun setButtonText(buttonRes: Int) {
        button.text = resources.getText(buttonRes)
    }

    fun setIcon(iconRes: Int) {
        icon.setImageResource(iconRes)
    }

    fun onButtonClick(onClick: () -> Unit) {
        button.setOnClickListener {
            onClick()
        }
    }

    // TODO write test
    fun hideButton() {
        button.visibility = View.GONE
    }
}
