package com.stukalov.staffairlines.pro

import com.appsflyer.AppsFlyerConversionListener
import android.util.Log
import android.widget.Toast
import com.adapty.Adapty
import com.appsflyer.AppsFlyerLib

class ConversionLietener : AppsFlyerConversionListener {
    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
        var message = ""

        if (!p0.isNullOrEmpty()) {
            message = p0.toString()

            Adapty.updateAttribution(p0, "appsflyer") { error ->
                if (error != null) {
                    Log.d("onConversionDataSuccess", error.message!! )
                }
            }
        }

        Log.d("OnConversionDataSuccess", message)
    }

    override fun onConversionDataFail(p0: String?) {
        Log.d("OnInstallConversionFailure", p0!!)
    }

    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        var message = ""
        if (!p0.isNullOrEmpty()) {
            message = p0.toString()
        }
        Log.d("OnAppOpenAttribution", message)
    }

    override fun onAttributionFailure(p0: String?) {
        Log.d("OnAttributionFailure", p0!!)
    }
}