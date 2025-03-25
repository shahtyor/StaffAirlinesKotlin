package com.stukalov.staffairlines.pro.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stukalov.staffairlines.pro.BottomMenuAdapter
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.databinding.FragmentProfilesetBinding

const val ARG_OBJECT = "object"

class MainFragment : Fragment() {
    private var _binding: FragmentProfilesetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    lateinit var adapter: BottomMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.pager_main)
        tabLayout = view.findViewById(R.id.tab_layout_main)

        adapter = BottomMenuAdapter(this)
        viewPager.setAdapter(adapter)

        val tabNames: Array<String> = arrayOf(
            "Flights",
            "Favourites",
            "History",
            "Profile"
        )

        val tabIcons: Array<Int> = arrayOf(
            R.drawable.flights2,
            R.drawable.favfooter,
            R.drawable.history2,
            R.drawable.profile4
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
            tab.setIcon(tabIcons[position])
        }.attach()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}