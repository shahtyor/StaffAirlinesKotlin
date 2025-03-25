package com.stukalov.staffairlines.pro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stukalov.staffairlines.pro.ui.favourites.FavouritesFragment
import com.stukalov.staffairlines.pro.ui.history.HistoryFragment
import com.stukalov.staffairlines.pro.ui.home.HomeFragment
import com.stukalov.staffairlines.pro.ui.setting.ProfileFragment

class BottomMenuAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {

        if (position == 0) {
            return HomeFragment()
        }
        else if (position == 1)
        {
            return FavouritesFragment()
        }
        else if (position == 2)
        {
            return HistoryFragment()
        }
        else
        {
            return ProfileFragment()
        }
    }
}