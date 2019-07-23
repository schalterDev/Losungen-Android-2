package de.schalter.customize

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import androidx.preference.PreferenceDialogFragmentCompat


class ChooseStylePreferenceDialog : PreferenceDialogFragmentCompat(), AdapterView.OnItemClickListener {

    override fun onCreateDialogView(context: Context): View {
        return DialogView(context)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        (preference as ChooseStylePreference).apply {
            view.findViewById<GridView>(R.id.gridView).let { gridView ->
                gridView.adapter = ChooseStylePreferenceDialogGridAdapter(Customize.getAllThemes(gridView.context), this.titleToShow)
                gridView.onItemClickListener = this@ChooseStylePreferenceDialog
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        // will never be called with positive result because dialog closes when clicking on item
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        (preference as ChooseStylePreference).apply {
            this.persistValue(position)
        }

        this.activity?.let {
            this.dismiss()
            if (it is CustomizeActivity) {
                it.setCustomizeTheme()
            }
            it.recreate()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChooseStylePreferenceDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, Customize.PREFERENCE_TAG)
            }
        }
    }

    private class DialogView : FrameLayout {
        constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
        constructor(context: Context) : super(context)

        init {
            val view = View.inflate(context, R.layout.theme_picker, null)
            addView(view)
        }
    }
}