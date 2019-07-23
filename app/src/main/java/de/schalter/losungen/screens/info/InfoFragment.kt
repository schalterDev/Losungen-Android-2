package de.schalter.losungen.screens.info

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.psdev.licensesdialog.LicensesDialog
import de.schalter.customize.Customize
import de.schalter.losungen.R
import de.schalter.losungen.firebase.AnalyticsFragment
import de.schalter.losungen.utils.AppSystemData
import de.schalter.losungen.utils.Constants
import de.schalter.losungen.utils.Open


/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : AnalyticsFragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)

        mContext = view.context

        val textViews: MutableList<TextView> = mutableListOf()

        val textAppVersion = mContext.getString(R.string.app_version, AppSystemData.getAppVersion(view.context))
        textViews.add(view.findViewById<TextView>(R.id.textView_app_version).apply {
            this.text = textAppVersion
        })

        textViews.add(view.findViewById<TextView>(R.id.textView_licenses).apply {
            this.setOnClickListener { openLicenseDialog() }
        })

        textViews.add(view.findViewById<TextView>(R.id.textView_translate).apply {
            this.setOnClickListener { openWebsiteForTranslation() }
        })
        textViews.add(view.findViewById<TextView>(R.id.textView_martin_schalter).apply {
            this.setOnClickListener { openWebsiteOfDeveloper() }
        })
        textViews.add(view.findViewById<TextView>(R.id.textView_github).apply {
            this.setOnClickListener { openGithubPage() }
        })

        textViews.forEach {
            setTintedCompoundDrawable(it)
        }

        return view
    }

    private fun openLicenseDialog() =
            LicensesDialog.Builder(mContext)
                    .setNotices(R.raw.notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show()

    private fun openWebsiteForTranslation() = Open.website(mContext, Constants.urlTranslation)

    private fun openWebsiteOfDeveloper() = Open.website(mContext, Constants.urlProgrammer)

    private fun openGithubPage() = Open.website(mContext, Constants.urlGitHub)

    private fun setTintedCompoundDrawable(textView: TextView) {
        textView.compoundDrawablesRelative.forEach { drawable ->
            drawable?.setColorFilter(Customize.getColor(textView.context, Customize.FONT), PorterDuff.Mode.SRC_IN)
        }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         *
         * @return A new instance of fragment InfoFragment.
         */
        fun newInstance(): InfoFragment {
            return InfoFragment()
        }
    }

}
