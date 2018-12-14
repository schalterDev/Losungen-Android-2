package schalter.de.losungen2.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

open class FragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragmentWithTitleList: MutableList<FragmentWithTitle> = mutableListOf()

    override fun getPageTitle(position: Int): CharSequence? = fragmentWithTitleList[position].title

    override fun getItem(position: Int): Fragment = fragmentWithTitleList[position].fragment

    override fun getCount(): Int = fragmentWithTitleList.size

    override fun getItemPosition(`object`: Any): Int {
        // TODO (save a id of each fragment in an array and determine the position of the fragment and return it)
        return PagerAdapter.POSITION_NONE
    }

    fun addItemAtStart(itemToAdd: FragmentWithTitle) = this.addItemAtPosition(0, itemToAdd)

    fun addItemAtEnd(itemToAdd: FragmentWithTitle) = this.addItemAtPosition(fragmentWithTitleList.size - 1, itemToAdd)

    fun addItemAtPosition(position: Int, itemToAdd: FragmentWithTitle) {
        var positionInArray = position
        if (positionInArray < 0)
            positionInArray = 0
        fragmentWithTitleList.add(positionInArray, itemToAdd)
        notifyDataSetChanged()
    }

    data class FragmentWithTitle(val fragment: Fragment, val title: String)
}