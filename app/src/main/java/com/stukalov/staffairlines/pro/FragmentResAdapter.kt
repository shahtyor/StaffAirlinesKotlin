package com.stukalov.staffairlines.pro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.stukalov.staffairlines.pro.ui.result.ResultFragment
import com.stukalov.staffairlines.pro.ui.result.TransferFragment
import com.stukalov.staffairlines.pro.ui.setting.CredentialsFragment
import com.stukalov.staffairlines.pro.ui.setting.SettingFragment

class FragmentResAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = ResultFragment()
        } else if (position == 1) {
            fragment = TransferFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Direct flights"
        } else if (position == 1) {
            title = "Transfers"
        }
        return title
    }
}