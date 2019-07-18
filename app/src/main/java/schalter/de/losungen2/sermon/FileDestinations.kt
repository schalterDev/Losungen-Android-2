package schalter.de.losungen2.sermon

import android.content.Context
import android.os.Build
import java.io.File

object FileDestinations {
    fun getPrivatePath(context: Context): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.noBackupFilesDir
        } else {
            return context.filesDir
        }
    }

    fun getCachePath(context: Context): File {
        return context.cacheDir
    }
}