package schalter.de.customize

import android.app.Activity
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.TypedValue
import android.view.WindowManager

object Customize {
    private const val PREFERENCE_TAG = "selected_theme"

    private const val BLUE = 0
    private const val YELLOW = 1
    private const val GREEN = 2
    private const val RED = 3

    private const val DARK = 4
    private const val LIGHT = 5

    val PRIMARY = R.attr.colorPrimary
    val PRIMARY_DARK = R.attr.colorPrimaryDark
    val PRIMARY_LIGHT = R.attr.colorPrimaryLight
    val ACCENT = R.attr.colorAccent
    val FONT = R.attr.colorFont
    val FONT_DISABLED = R.attr.colorFontDisabled
    val SECOND_FONT = R.attr.colorSecondFont
    val ICONS_TOOLBAR = R.attr.colorIconsToolbar
    val BACKGROUND = R.attr.colorBackground
    val WINDOW_BACKGROUND = R.attr.colorWindowBackground

    fun getColor(context: Context, attrColor: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attrColor, typedValue, true)

        return typedValue.data
    }

    fun getTheme(context: Context): Int {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            return when (this.getInt(PREFERENCE_TAG, 1)) {
                BLUE -> R.style.AppTheme_Blue
                YELLOW -> R.style.AppTheme_Yellow
                GREEN -> R.style.AppTheme_Green
                RED -> R.style.AppTheme_Red
                DARK -> R.style.AppTheme_Dark
                LIGHT -> R.style.AppTheme_Light
                else -> R.style.AppTheme_Blue
            }
        }

    }

    fun setStatusBarColor(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = getColor(activity, color)
        }

    }
}