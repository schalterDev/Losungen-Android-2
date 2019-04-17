package schalter.de.losungen2.utils

import android.app.Application
import schalter.de.losungen2.R

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }

}