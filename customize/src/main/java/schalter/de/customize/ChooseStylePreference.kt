package schalter.de.customize

import android.content.Context
import android.util.AttributeSet

class ChooseStylePreference(context: Context, attrs: AttributeSet) : androidx.preference.DialogPreference(context, attrs) {

    var titleToShow: String = "Title"

    init {
        val titleToShowResource = attrs.getAttributeResourceValue(null, "titleToolbar", -1)
        if (titleToShowResource != -1) {
            titleToShow = context.resources.getString(titleToShowResource)
        }
    }

    fun persistValue(style: Int) {
        persistInt(style)
        notifyChanged()

        dialogLayoutResource = R.layout.theme_picker_item
    }

}