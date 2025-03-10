package com.stukalov.staffairlines.pro.ui.sel_ac

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.PermittedAC
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.ShowPermittAdapter
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentPermittedAcBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowPermittFragment : Fragment() {

    private var _binding: FragmentPermittedAcBinding? = null
    lateinit var aclistadapter: ShowPermittAdapter
    lateinit var ac_lv: ListView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPermittedAcBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    fun GetData(text: String): MutableList<PermittedAC> {

        val tmpLoc = GlobalStuff.Permitted
        var tmp = tmpLoc.toMutableList()

        return tmp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ac_lv = view.findViewById<ListView>(R.id.permittlistview)

        GlobalStuff.navView!!.visibility = View.GONE

        try {

            val tmp0 = GetData(GlobalStuff.OwnAC!!.Code)

            if (tmp0.count() > 0) {
                aclistadapter =
                    ShowPermittAdapter(view.context, ArrayList(tmp0))
                ac_lv.setAdapter(aclistadapter)
            }
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
    }

    override fun onStart() {
        super.onStart()

        //var LocList = StaffApp.Locations
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}