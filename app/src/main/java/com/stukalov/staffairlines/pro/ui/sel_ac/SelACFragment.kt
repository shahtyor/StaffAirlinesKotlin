package com.stukalov.staffairlines.pro.ui.sel_ac

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.Airline0
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelACAdapter
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentSelAcBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelACFragment : Fragment() {

    private var _binding: FragmentSelAcBinding? = null
    lateinit var aclistadapter: SelACAdapter
    var strmode: String? = null
    lateinit var ac_lv: ListView
    lateinit var cont: Context
    val SM: StaffMethods = StaffMethods()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val selPointViewModel =
            ViewModelProvider(this).get(SelPointViewModel::class.java)*/

        _binding = FragmentSelAcBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.selpointtext

        return root
    }

    fun GetData(text: String): MutableList<Airline0> {
        val tmpLoc = GlobalStuff.Airlines
        var tmp = tmpLoc.toMutableList()

        if (text.length == 2) {
            tmp = tmpLoc.asSequence().filter {
                it.Airline.uppercase().contains(text) && it.Code != text
            }.take(20).toMutableList()

            val liata =
                tmpLoc.asSequence().filter { it.Code == text }.take(1)
                    .firstOrNull();

            if (liata != null) {
                tmp.add(0, liata)
            }
        } else {
            if (text.isNotEmpty()) {
                tmp = tmpLoc.asSequence().filter {
                    it.Airline.uppercase().contains(text)
                }.take(20).toMutableList()
            }
        }
        return tmp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ac_lv = view.findViewById<ListView>(R.id.aclistview)
        cont = view.context

        GlobalStuff.navView.visibility = View.GONE

        strmode = getArguments()?.getString("SelACMode")

        try {

            val tmp0 = GetData("")

            if (tmp0.count() > 0) {
                aclistadapter =
                    SelACAdapter(view.context, ArrayList(tmp0))
                ac_lv.setAdapter(aclistadapter)
            }

            val sel_ac_text = view.findViewById<EditText>(R.id.selactext)

            sel_ac_text.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int) {

                    val text = sel_ac_text.text.toString().uppercase()

                    val tmp = GetData(text)

                    if (tmp.count() > 0) {
                        aclistadapter =
                            SelACAdapter(view.context, ArrayList(tmp))
                        ac_lv.setAdapter(aclistadapter)
                    }
                }
            })

            ac_lv.setOnItemClickListener{parent, view, position, id ->
                val ac = parent.getItemAtPosition(position) as Airline0

                AlertDialog.Builder(parent.context)
                    .setTitle("Confirm choice")
                    .setMessage("Your choice is " + ac.Airline + " (" + ac.Code + "). Is this correct?")
                    .setPositiveButton("sure") { dialog, id -> NextStep(dialog, parent.context, ac) }
                    .setNegativeButton("no") { dialog, id -> dialog.cancel() }
                    .show()
             }
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
    }

    fun NextStep(dialog: DialogInterface, cont: Context, ac: Airline0, )
    {
        dialog.cancel()

        AlertDialog.Builder(cont)
            .setTitle("Congratulations!")
            .setMessage("Now you can start using the app. Please note, that you will find all represented airlines in the app by default. If want to limit the range of available airlines, please send us list of allowed airlines for your staff at hello@staffairlines.com. Any format is OK. We will add these carriers to the preset for " + ac.Airline + ".")
            .setNegativeButton("ok") { dialog, id -> NextStep2(dialog, ac) }
            .show()
    }

    fun NextStep2(dialog: DialogInterface, ac: Airline0) {
        dialog.cancel()

        GlobalStuff.OwnAC = ac
        SM.SaveOwnAC()

        lifecycleScope.launch {
            val jsonperm = withContext(Dispatchers.IO) { SM.GetPermittedAC(GlobalStuff.OwnAC!!.Code) }
        }

        GlobalStuff.navController.navigateUp()
        GlobalStuff.navView.isVisible = true
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