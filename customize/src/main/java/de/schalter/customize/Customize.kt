package de.schalter.customize

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import android.util.TypedValue
import android.view.WindowManager


object Customize {
    const val PREFERENCE_TAG = "selected_theme"

    private const val THEME_BLUE = 0
    private const val THEME_YELLOW = 1
    private const val THEME_GREEN = 2
    private const val THEME_RED = 3
    private const val THEME_DARK = 4
    private const val THEME_LIGHT = 5

    private val THEMES_RESOURCES = arrayListOf(
            R.style.Theme_Blue,
            R.style.Theme_Yellow,
            R.style.Theme_Green,
            R.style.Theme_Red,
            R.style.Theme_Gray,
            R.style.Theme_Dark,
            R.style.Theme_Light
    )

    private var actualStyle: Int? = null

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

    fun getColor(context: Context, attrColor: Int, theme: Resources.Theme? = null): Int {
        val typedValue = TypedValue()
        val themeToUse = if (theme == null) {
            context.theme
        } else {
            theme
        }
        themeToUse.resolveAttribute(attrColor, typedValue, true)

        return typedValue.data
    }

    fun getThemeData(context: Context, themeRes: Int): CustomizeTheme {
        val theme = context.resources.newTheme()
        theme.applyStyle(themeRes, true)

        return CustomizeTheme(
                primary = getColor(context, PRIMARY, theme),
                accent = getColor(context, ACCENT, theme),
                toolbarIcon = getColor(context, ICONS_TOOLBAR, theme),
                windowBackground = getColor(context, WINDOW_BACKGROUND, theme)
        )
    }

    fun getAllThemes(context: Context): List<CustomizeTheme> {
        return THEMES_RESOURCES.map { themeRes ->
            getThemeData(context, themeRes)
        }
    }

    fun getTheme(context: Context): Int {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            return THEMES_RESOURCES[this.getInt(PREFERENCE_TAG, 0)].also {
                actualStyle = it
            }
        }
    }

    fun themeChanged(oldStyle: Int): Boolean {
        return oldStyle != actualStyle
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