package schalter.de.losungen2.screens.info

import android.annotation.SuppressLint
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


/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {

    private lateinit var mContext: Context

    private lateinit var textViewAppVersion: TextView
    private lateinit var textViewLicenses: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)

        mContext = view.context

        textViewAppVersion = view.findViewById(R.id.textView_app_version)
        textViewAppVersion.text = mContext.getString(R.string.app_version) +
                " " + AppSystemData.getAppVersion(view.context).toString()

        textViewLicenses = view.findViewById(R.id.textView_licenses)
        textViewLicenses.setOnClickListener { openLicenseDialog() }

        return view
    }

    private fun openLicenseDialog() {
        LicensesDialog.Builder(mContext)
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .show()
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
