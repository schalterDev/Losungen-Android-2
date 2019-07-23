package schalter.de.customize

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class CustomizeActivity : AppCompatActivity() {

    private var themeRes: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setCustomizeTheme()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        themeRes?.let {
            if (Customize.themeChanged(it))
                recreate()
        }
    }

    fun setCustomizeTheme() {
        setTheme(Customize.getTheme(this).also { themeRes = it })
    }
}