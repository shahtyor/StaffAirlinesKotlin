package com.stukalov.staffairlines.pro.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.stukalov.staffairlines.pro.FragmentResAdapter
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.databinding.FragmentResulttransBinding

const val ARG_OBJECT = "object"

class ResultTransFragment : Fragment() {
    private var _binding: FragmentResulttransBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    lateinit var adapter: FragmentResAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResulttransBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    private fun setupViewPager(viewpager: ViewPager) {
        adapter = FragmentResAdapter(childFragmentManager)
        viewpager.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.pager_resulttrans)
        tabLayout = view.findViewById(R.id.tab_layout_resulttrans)

        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = tabLayout.selectedTabPosition
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
