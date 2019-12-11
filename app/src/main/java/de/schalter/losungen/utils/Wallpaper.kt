package de.schalter.losungen.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import de.schalter.losungen.R

object Wallpaper {

    fun getWallpaperDrawable(context: Context): Drawable {
        // WallpaperManager.getInstance(context).drawable
        return ContextCompat.getDrawable(context, R.drawable.widget_preview_background)!!
    }

}