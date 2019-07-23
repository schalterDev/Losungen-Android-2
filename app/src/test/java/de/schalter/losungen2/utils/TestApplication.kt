package de.schalter.losungen2.utils

import android.app.Application
import de.schalter.losungen2.R

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }

}