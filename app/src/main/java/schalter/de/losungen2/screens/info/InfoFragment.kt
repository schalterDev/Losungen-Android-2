package schalter.de.losungen2.screens.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.psdev.licensesdialog.LicensesDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.AppSystemData
import schalter.de.losungen2.utils.Constants
import schalter.de.losungen2.utils.Open


/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)

        mContext = view.context

        val textAppVersion = mContext.getString(R.string.app_version, AppSystemData.getAppVersion(view.context))
        view.findViewById<TextView>(R.id.textView_app_version).text = textAppVersion

        view.findViewById<TextView>(R.id.textView_licenses).setOnClickListener { openLicenseDialog() }
        view.findViewById<TextView>(R.id.textView_translate).setOnClickListener { openWebsiteForTranslation() }
        view.findViewById<TextView>(R.id.textView_martin_schalter).setOnClickListener { openWebsiteOfDeveloper() }
        view.findViewById<TextView>(R.id.textView_github).setOnClickListener { openGithubPage() }

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
