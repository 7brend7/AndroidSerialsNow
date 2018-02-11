package com.brend.serialsnow.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.brend.serialsnow.models.Category
import com.brend.serialsnow.views.TabFragment

class TabPagerAdapter(fm: FragmentManager, private var categories: ArrayList<Category>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = TabFragment.newInstance(position)

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence = categories[position].title

    fun replaceData(data: ArrayList<Category>) {
        categories = data
    }
}