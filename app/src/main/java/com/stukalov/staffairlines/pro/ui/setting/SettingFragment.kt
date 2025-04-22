package com.stukalov.staffairlines.pro.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val set_ac = view.findViewById<TextView>(R.id.setting_yourac)
        val set_permit = view.findViewById<TextView>(R.id.setting_permitted)

        if (GlobalStuff.OwnAC != null)
        {
            set_ac.setText(GlobalStuff.OwnAC!!.Airline + " (" + GlobalStuff.OwnAC!!.Code + ")")
        }

        set_ac.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("SelACMode", "setting")
            GlobalStuff.navController.navigate(R.id.sel_ac_frag, bundle)
        }

        set_permit.setOnClickListener {
            val bundle = Bundle()
            GlobalStuff.navController.navigate(R.id.show_permitt_frag, bundle)
        }
    }

    override fun onResume() {
        super.onResume()

        // Открылась форма настроек
        val event = GlobalStuff.GetBaseEvent("Settings open", true, true)
        GlobalStuff.amplitude?.track(event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}