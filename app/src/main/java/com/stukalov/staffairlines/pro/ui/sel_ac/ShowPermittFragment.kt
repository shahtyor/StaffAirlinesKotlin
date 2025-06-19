package com.stukalov.staffairlines.pro.ui.sel_ac

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
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

        val tmpLoc = GlobalStuff.Permitted.sortedBy { it.Permit }
        var tmp = tmpLoc.toMutableList()

        return tmp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ac_lv = view.findViewById<ListView>(R.id.permittlistview)
        val text1 = view.findViewById<TextView>(R.id.perText1)
        val text2 = view.findViewById<TextView>(R.id.perText2)

        val t1 = "Permitted Airlines For " + GlobalStuff.OwnAC?.Code
        text1.setText(t1)

        var t2 = ""
        if (GlobalStuff.Permitted.isEmpty())
        {
            t2 = "At this time, we do not have information on a list of airlines that provide SA fare tickets for All Airlines employees. Therefore, the search results currently show flights of all airlines flying to this destination.<br /><>br />" +
                    "You can forward us the list of available airlines to <a href='mailto:hello@staffairlines.com'>hello@staffairlines.com</a> and we will upload it to the system. After that you will be able to filter the search results by the airlines that are available to you.<br /><br />" +
                    "Be the first from your airline to send us a valid list, and we'll reward you with a 1-month premium subscription!"
        }
        else {
            t2 = "According to our data, employees of " + GlobalStuff.OwnAC?.Airline + " have access to SA fares on flights of airlines from the list below. If this list is not complete or contains errors, please mail to us <a href='mailto:hello@staffairlines.com'>hello@staffairlines.com</a>"
        }
        text2.setText(Html.fromHtml(t2))
        text2.setMovementMethod(LinkMovementMethod.getInstance())
        text2.setLinkTextColor(GlobalStuff.StaffRes.getColor(R.color.staff_blue, null))
        text2.setTextColor(GlobalStuff.StaffRes.getColor(R.color.black, null))

        GlobalStuff.setActionBar(true, true, "Permitted airlines")

        try {

            val tmp0 = GetData(GlobalStuff.OwnAC!!.Code)

            if (tmp0.count() > 0) {
                aclistadapter = ShowPermittAdapter(view.context, ArrayList(tmp0))
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}