package de.schalter.losungen.screens.intro

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import de.schalter.losungen.R


class IntroCustomFragment : Fragment(), ISlideBackgroundColorHolder {

    private lateinit var title: String
    private var imageResource: Int? = null
    private var backgroundColor: Int = Color.WHITE
    private var foregroundColor: Int = Color.BLACK
    private var subtitle: String? = null
    private var description: String? = null
    private var switchText: String? = null
    private var switchForegroundColor: Int? = null
    private var switchChecked: Boolean = false
    private var switchPreferenceTag: String? = null
    private var switchPreferenceCheckChanged: ((isChecked: Boolean) -> Unit)? = null

    private lateinit var container: LinearLayout
    private lateinit var viewTitle: TextView
    private lateinit var viewSwitch: SwitchCompat
    private lateinit var viewImage: ImageView
    private lateinit var viewSubtitle: TextView
    private lateinit var viewDescription: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro_custom_fragment, container, false).also {
            this.container = it.findViewById(R.id.intro_container)
            this.viewTitle = it.findViewById(R.id.intro_title)
            this.viewSwitch = it.findViewById(R.id.intro_switch)
            this.viewImage = it.findViewById(R.id.intro_image)
            this.viewSubtitle = it.findViewById(R.id.intro_subtitle)
            this.viewDescription = it.findViewById(R.id.intro_description)

            setTitle()
            setSubtitle()
            setDescription()
            setImageResource()
            setupSwitch()
            setColors()
        }
    }

    private fun setTitle() {
        this.viewTitle.text = title
    }

    private fun setSubtitle() {
        if (subtitle != null) {
            this.viewSubtitle.text = subtitle
        } else {
            this.viewSubtitle.visibility = View.GONE
        }
    }

    private fun setDescription() {
        if (description != null) {
            this.viewDescription.text = description
        } else {
            this.viewDescription.visibility = View.GONE
        }
    }

    private fun setImageResource() {
        this.viewImage.setImageResource(imageResource!!)
    }

    private fun setupSwitch() {
        if (switchText != null && (switchPreferenceTag != null || switchPreferenceCheckChanged != null)) {
            this.viewSwitch.text = switchText
            this.viewSwitch.isChecked = switchChecked

            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            this.viewSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (switchPreferenceTag != null) {
                    preference.edit().putBoolean(switchPreferenceTag, isChecked).apply()
                }

                switchPreferenceCheckChanged?.let { it(isChecked) }
            }
        } else {
            viewSwitch.visibility = View.GONE
        }
    }

    private fun setColors() {
        setBackgroundColor(backgroundColor)

        viewTitle.setTextColor(foregroundColor)
        viewSubtitle.setTextColor(foregroundColor)
        viewDescription.setTextColor(foregroundColor)
        viewSwitch.setTextColor(foregroundColor)

        // for switch
        val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

        val thumbColors = intArrayOf(foregroundColor, switchForegroundColor ?: foregroundColor)
        DrawableCompat.setTintList(DrawableCompat.wrap(viewSwitch.thumbDrawable), ColorStateList(states, thumbColors))

//        val trackColors = intArrayOf(Color.GREEN, Color.BLUE)
//        DrawableCompat.setTintList(DrawableCompat.wrap(viewSwitch.trackDrawable), ColorStateList(states, trackColors))
    }

    override fun getDefaultBackgroundColor(): Int = backgroundColor

    override fun setBackgroundColor(backgroundColor: Int) {
        this.container.setBackgroundColor(backgroundColor)
    }

    companion object {

        fun newInstance(
                title: String,
                imageResource: Int,
                backgroundColor: Int,
                foregroundColor: Int,
                subtitle: String? = null,
                description: String? = null,
                switchText: String? = null,
                switchForegroundColor: Int? = null,
                switchChecked: Boolean = false,
                switchPreferenceTag: String? = null,
                switchPreferenceCheckChanged: ((isChecked: Boolean) -> Unit)? = null
        ): IntroCustomFragment {
            return IntroCustomFragment().apply {
                this.title = title
                this.subtitle = subtitle
                this.imageResource = imageResource
                this.description = description
                this.switchText = switchText
                this.switchForegroundColor = switchForegroundColor
                this.switchChecked = switchChecked
                this.switchPreferenceTag = switchPreferenceTag
                this.switchPreferenceCheckChanged = switchPreferenceCheckChanged
                this.backgroundColor = backgroundColor
                this.foregroundColor = foregroundColor
            }
        }
    }

}