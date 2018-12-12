package schalter.de.losungen2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import schalter.de.losungen2.R

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
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
