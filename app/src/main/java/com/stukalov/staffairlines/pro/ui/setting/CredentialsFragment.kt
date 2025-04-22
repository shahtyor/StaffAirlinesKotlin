package com.stukalov.staffairlines.pro.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.adapty.Adapty
import com.adapty.models.AdaptyProfileParameters
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentCredentialsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CredentialsFragment : Fragment() {

    private var _binding: FragmentCredentialsBinding? = null
    val SM: StaffMethods = StaffMethods()
    lateinit var tvCredLogin: TextView
    lateinit var tvCredFirst: TextView
    lateinit var tvCredSecond: TextView
    lateinit var tvCredDelete: TextView
    lateinit var llCredID: LinearLayout
    lateinit var tvCredID: TextView
    lateinit var ivCredID: ImageView
    lateinit var tvCredCoins: TextView
    lateinit var ivCredCoins: ImageView
    lateinit var tvCredSubLost: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCredentialsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ServiceCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCredLogin = view.findViewById(R.id.tvCredLogin)
        tvCredFirst = view.findViewById(R.id.tvCredFirst)
        tvCredSecond = view.findViewById(R.id.tvCredSecond)
        tvCredDelete = view.findViewById(R.id.tvCredDelete)
        llCredID = view.findViewById(R.id.llCredID)
        tvCredID = view.findViewById(R.id.tvCredID)
        ivCredID = view.findViewById(R.id.ivCredID)
        tvCredCoins = view.findViewById(R.id.tvCredCoins)
        ivCredCoins = view.findViewById(R.id.ivCredCoins)
        tvCredSubLost = view.findViewById(R.id.tvCredSubLost)

        val pInfo: PackageInfo = view.context.getPackageManager()!!.getPackageInfo(view.context.getPackageName(), 0)

        ivCredID.setOnClickListener()
        {
            val clipboard =
                GlobalStuff.mActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ID", pInfo.versionName + "." + pInfo.versionCode + ":" + GlobalStuff.customerID?.removeRange(0, 2))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(GlobalStuff.activity, "Copied!", Toast.LENGTH_LONG).show()
        }

        var strtoken = ""
        if (!GlobalStuff.customerID.isNullOrEmpty()) {
            lifecycleScope.launch {
                val tok = withContext(Dispatchers.IO) { SM.GenToken(GlobalStuff.customerID!!) }

                if (tok != null) {
                    strtoken = tok.token
                    tvCredSecond.setText(Html.fromHtml("If you want to become an agent, post flight load data and get your own benefits, go to the <a href='https://t.me/DevStaffCommunityBot?start=" + strtoken + "' style='color: #8ebf42'><u>telegram bot</u></a>. More about agents read <a href='https://staffairlines.com/flightclub'><u>here</u></a>."))
                }
            }
        }

        tvCredDelete.setText(Html.fromHtml("<u>Delete profile</u>"))
        tvCredSecond.setMovementMethod(LinkMovementMethod.getInstance())
        tvCredSecond.setLinkTextColor(GlobalStuff.StaffRes.getColor(R.color.staff_blue, null))
        tvCredSecond.setTextColor(GlobalStuff.StaffRes.getColor(R.color.black, null))

        Init(view.context)
        GlobalStuff.CF = this
    }

    fun Init(context: Context)
    {
        tvCredLogin.setOnClickListener()
        {
            if (GlobalStuff.customerID.isNullOrEmpty()) {
                GlobalStuff.navController.navigate(R.id.login_frag, Bundle())
            }
            else {
                val t0 = GlobalStuff.premiumAccess
                Adapty.logout { error ->
                    if (error == null) {
                        // successful logout
                        GlobalStuff.premiumAccess = t0
                        GlobalStuff.customerProfile = null
                        GlobalStuff.customerID = null
                        GlobalStuff.customerEmail = null
                        GlobalStuff.customerLastName = null
                        GlobalStuff.customerFirstName = null

                        GlobalStuff.googleInClient?.signOut()
                        SM.SaveCustomerID()
                        Init(context)
                    }
                }
                AppsFlyerLib.getInstance().setCustomerUserId(null)
                OneSignal.logout()
                GlobalStuff.SaveOneSignalToAdapty()
            }
        }

        tvCredDelete.setOnClickListener()
        {
            AlertDialog.Builder(context)
                .setTitle("Delete profile?")
                .setMessage("Deleting a profile will delete all your data, you will lose all your purchased or earned tokens. After deleting an account, they will not be refundable. Please be informed.")
                .setPositiveButton("Delete") { dialog, id -> DeleteProfile(dialog, this.requireContext()) }
                .setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }
                .show()
        }

        if (GlobalStuff.customerID.isNullOrEmpty())
        {
            tvCredFirst.setText("To make requests to an agent, please logged in")
            tvCredLogin.setText(Html.fromHtml("<u>Log in</u>"))
            tvCredSecond.visibility = View.GONE
            tvCredID.setText("")
            llCredID.visibility = View.GONE
            tvCredDelete.visibility = View.GONE
            tvCredCoins.visibility = View.GONE
            ivCredCoins.visibility = View.GONE
        }
        else
        {
            tvCredFirst.setText("You have successfully logged in via Google")
            tvCredLogin.setText(Html.fromHtml("<u>Log out</u>"))
            tvCredSecond.visibility = View.VISIBLE

            val pInfo: PackageInfo = context.getPackageManager()!!.getPackageInfo(context.getPackageName(), 0)
            //String version = pInfo.versionName;

            tvCredID.setText("ID for support: " + pInfo.versionName + "." + pInfo.versionCode + ":" + GlobalStuff.customerID?.removeRange(0, 2))
            llCredID.visibility = View.VISIBLE
            tvCredDelete.visibility = View.VISIBLE
            tvCredCoins.visibility = View.VISIBLE
            ivCredCoins.visibility = View.VISIBLE

            if (GlobalStuff.premiumAccess)
            {
                if (GlobalStuff.customerProfile != null) {
                    val sum = GlobalStuff.customerProfile?.SubscribeTokens?.plus(GlobalStuff.customerProfile?.NonSubscribeTokens!!)
                    tvCredCoins.setText(sum.toString())
                }
            }
            else
            {
                tvCredCoins.setText("0")
            }
        }

        if (GlobalStuff.premiumAccess) {
            tvCredSubLost.setText(GlobalStuff.Remain.toString())
        } else {
            tvCredSubLost.setText("0")
        }
    }

    fun DeleteProfile(dialog: DialogInterface, cont: Context)
    {
        //await PremiumFunctions.SendEvent("delete profile");

        val aprof = AdaptyProfileParameters.Builder()
            .withEmail(null)
            .withFirstName(null)
            .withLastName(null)
            .withRemovedCustomAttribute("own_ac")

        Adapty.updateProfile(aprof.build()) { error2 ->
            if (error2 != null) {
                AlertDialog.Builder(cont)
                    .setTitle("Delete")
                    .setMessage("Update profile failed: " + error2.message)
                    .setNegativeButton("ok") { dialog, id -> dialog.cancel() }
                    .show()
            }
            else
            {
                lifecycleScope.launch {
                    val voidresult = withContext(Dispatchers.IO) {
                        SM.VoidProfile(GlobalStuff.customerID!!)
                    }

                    if (voidresult.Success)
                    {
                        Adapty.logout { error ->
                            if (error == null) {
                                // successful logout
                                GlobalStuff.customerProfile = null
                                GlobalStuff.customerID = null
                                GlobalStuff.customerEmail = null
                                GlobalStuff.customerLastName = null
                                GlobalStuff.customerFirstName = null

                                GlobalStuff.googleInClient?.signOut()
                                SM.SaveCustomerID()
                                Init(cont)

                                AlertDialog.Builder(cont)
                                    .setTitle("Delete")
                                    .setMessage("Profile deleted successfully")
                                    .setNegativeButton("ok") { dialog3, id -> dialog3.cancel() }
                                    .show()
                            }
                        }

                        OneSignal.logout()
                        GlobalStuff.SaveOneSignalToAdapty()
                    }
                    else
                    {
                        //await PremiumFunctions.SendEventError(tmpID, "delete profile", res.Error, "");
                        AlertDialog.Builder(cont)
                            .setTitle("Delete")
                            .setMessage(voidresult.Error)
                            .setNegativeButton("OK") { dialog4, id -> dialog4.cancel() }
                            .show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}