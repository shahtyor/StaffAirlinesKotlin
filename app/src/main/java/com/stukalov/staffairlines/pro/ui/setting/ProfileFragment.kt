package com.stukalov.staffairlines.pro.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.stukalov.staffairlines.pro.FragmentAdapter
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentProfilesetBinding

const val ARG_OBJECT = "object"

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfilesetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    lateinit var adapter: FragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfilesetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    private fun setupViewPager(viewpager: ViewPager) {
        adapter = FragmentAdapter(childFragmentManager)
        viewpager.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.pager_profileset)
        tabLayout = view.findViewById(R.id.tab_layout_profileset)

        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)
        //viewPager.currentItem = tabLayout.selectedTabPosition
        tabLayout.refreshDrawableState()
        viewPager.refreshDrawableState()
    }

    override fun onResume() {
        super.onResume()
        GlobalStuff.setActionBar(true, false, "Profile")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}