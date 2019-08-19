package de.schalter.customize

import android.app.Activity
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.forEach


class CustomizeToolbar : Toolbar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)


    init {
        Customize.getColor(context, Customize.ICONS_TOOLBAR).apply {
            colorizeToolbar(this@CustomizeToolbar, this)
        }

        if (context is Activity) {
            Customize.setStatusBarColor(context as Activity, Customize.PRIMARY_DARK)
        }
    }

    /**
     * Use this method to colorize toolbar icons to the desired target color
     *
     * @param toolbarView       toolbar view being colored
     * @param toolbarIconsColor the target color of toolbar icons
     */
    private fun colorizeToolbar(toolbarView: Toolbar, toolbarIconsColor: Int) {
        val colorFilter = PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN)

        toolbarView.children.forEach { v ->
            doColorizing(v, colorFilter, toolbarIconsColor)
        }

        toolbarView.setTitleTextColor(toolbarIconsColor)
        toolbarView.setSubtitleTextColor(toolbarIconsColor)
    }

    private fun doColorizing(v: View, colorFilter: ColorFilter, toolbarIconsColor: Int) {
        if (v is Button) {
            v.compoundDrawablesRelative.forEach { drawable ->
                drawable?.setColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN)
            }
            v.setTextColor(toolbarIconsColor)
        }

        if (v is ImageButton) {
            v.drawable.alpha = 255
            v.drawable.colorFilter = colorFilter
            v.setColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN)
        }

        if (v is ImageView) {
            v.drawable.alpha = 255
            v.drawable.colorFilter = colorFilter
            v.setColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN)
        }

        if (v is AutoCompleteTextView) {
            v.setTextColor(toolbarIconsColor)
        }

        if (v is TextView) {
            v.setTextColor(toolbarIconsColor)
        }

        if (v is EditText) {
            v.setTextColor(toolbarIconsColor)
        }

        if (v is ViewGroup) {
            v.forEach { child ->
                doColorizing(child, colorFilter, toolbarIconsColor)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (v is ActionMenuView) {
                v.forEach { innerView ->
                    if (innerView is ActionMenuItemView) {
                        innerView.compoundDrawables.forEach { compoundDrawable ->
                            compoundDrawable?.colorFilter = colorFilter
                        }
                        innerView.setTextColor(toolbarIconsColor)
                    }
                }
            }
        }
    }
}