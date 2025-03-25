package com.stukalov.staffairlines.pro.ui.carousel

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.stukalov.staffairlines.pro.CarouselData
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.CarouselAdapter
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentCarouselBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarouselFragment : Fragment() {

    private var _binding: FragmentCarouselBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCarouselBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        GlobalStuff.actionBar?.hide()

        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager_carousel)
        val btBegin = view.findViewById<Button>(R.id.btCarBegin)

        viewPager.apply {
            clipChildren = false  // No clipping the left and right items
            clipToPadding = false  // Show the viewpager in full width without clipping the padding
            offscreenPageLimit = 3  // Render the left and right items
            (getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect
            }

        val txt1 = "<font color='#333333'>Numbers show amounts of available seats. Color is our forecast to take a seat on board.</font><br /><font color='#92C55A'>Green</font><font color='#333333'> — your good chance!</font><br /><font color='#F9AA33'>Yellow</font><font color='#333333'> — so-so, have to be plan B.</font><br /><font color='#FF643B'>Red</font><font color='#333333'> — probably, no empty seats on this flight.</font>"
        val txt2 = "<font color='#333333'>Press to bell button in search details and get a push with update status of your flight.</font>"
        val txt3 = "<font color='#333333'>Now if you were bumped and couldn't flight out you can get plan B in seconds. We prepare for you list of stopovers and help to choose suitable flights.</font>"

        val CarItem1 = CarouselData("Flight loads data forecast about 500+ world airlines", "board1", Html.fromHtml(txt1))
        val CarItem2 = CarouselData("Get push messages with changes of your flights loads", "board2", Html.fromHtml(txt2))
        val CarItem3 = CarouselData("Create a backup plan in seconds", "board3", Html.fromHtml(txt3))
        val CarData: ArrayList<CarouselData> = arrayListOf(CarItem1, CarItem2, CarItem3)

        viewPager.adapter = CarouselAdapter(CarData)

        var tabLayout: TabLayout = view.findViewById(R.id.tab_layout_car);
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //Some implementation...
        }.attach()

        btBegin.setOnClickListener()
        {
            val SM: StaffMethods = StaffMethods()
            val bundle = Bundle()
            bundle.putString("SelACMode", "home")

            if (GlobalStuff.Airlines.size > 0)
            {
                GlobalStuff.actionBar?.show()
                GlobalStuff.navController.navigate(R.id.sel_ac_frag, bundle)
            }
            else {
                lifecycleScope.launch {
                    val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }

                    if (jsonair.isNotEmpty() && GlobalStuff.Airlines.size > 0) {
                        GlobalStuff.actionBar?.show()
                        GlobalStuff.navController.navigate(R.id.sel_ac_frag, bundle)
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}