package de.schalter.losungen.utils

import android.app.Application
import de.schalter.losungen.R

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }

}