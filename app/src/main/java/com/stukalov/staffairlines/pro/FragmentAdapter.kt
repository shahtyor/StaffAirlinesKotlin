package com.stukalov.staffairlines.pro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.stukalov.staffairlines.pro.ui.setting.CredentialsFragment
import com.stukalov.staffairlines.pro.ui.setting.SettingFragment

class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = CredentialsFragment()
        } else if (position == 1) {
            fragment = SettingFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Credentials"
        } else if (position == 1) {
            title = "Settings"
        }
        return title
    }
}