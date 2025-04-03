package com.stukalov.staffairlines.pro.ui.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
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
        //viewPager.currentItem = tabLayout.selectedTabPosition
        tabLayout.refreshDrawableState()
        viewPager.refreshDrawableState()
        GlobalStuff.tabRes = tabLayout

        /*if (!GlobalStuff.premiumAccess)
        {
            //tabLayout.getTabAt(1)?.view?.setEnabled(false)
            val v = tabLayout.getTabAt(1)?.view

            v?.setClickable(true)

            v?.setOnClickListener() {
                if (!GlobalStuff.premiumAccess)
                {
                    v.setEnabled(false)
                }

            }

            tabLayout.setOnClickListener()
            {
                val d = 234
            }

            /*val tabStrip = tabLayout.getChildAt(1) as ConstraintLayout
            val vg = tabStrip.getChildAt(1)
            val vg0 = tabStrip.getChildAt(0)

            vg.setOnTouchListener(object: View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View, event: MotionEvent): Boolean
                {
                    val ghj = "123"
                    return false
                }
            })*/

        }*/

        /*tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val pos = tab?.position

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val pos = tab?.position
                    if (pos == 0) {
                        val rf = adapter.getItem(0) as ResultFragment
                        val v = viewPager.get(0)
                        if (GlobalStuff.AdaptyPurchaseProcess == false) {
                            rf.LoadTransfer(v, tabLayout)
                        }
                        else
                        {
                            ///viewPager.setCurrentItem(0)
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    val klj = tab?.position
                }
            }
        )*/
    }

    override fun onResume() {
        super.onResume()
        //GlobalStuff.setActionBar(true, false, "Profile")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
