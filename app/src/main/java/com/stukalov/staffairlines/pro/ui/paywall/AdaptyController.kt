package com.stukalov.staffairlines.pro.ui.paywall

import android.os.Bundle
import com.adapty.Adapty
import com.adapty.ui.AdaptyUI
import com.adapty.utils.AdaptyResult
import com.adapty.utils.seconds
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R

class AdaptyController {

    fun GetPaywallViewParams(placement_name: String): String {
        var res = ""

        Adapty.getPaywall(placement_name, locale = "en", loadTimeout = 10.seconds) { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val paywall = result.value
                    GlobalStuff.AdaptyPaywallID = paywall.placementId
                    GlobalStuff.AdaptyPaywallRev = paywall.revision

                    Adapty.getPaywallProducts(paywall) { resprod ->
                        when (resprod) {
                            is AdaptyResult.Success -> {
                                val products = resprod.value

                                if (paywall.hasViewConfiguration) {
                                    AdaptyUI.getViewConfiguration(paywall, loadTimeout = 10.seconds) { r ->
                                        when (r) {
                                            is AdaptyResult.Success -> {
                                                val viewConfiguration = r.value

                                                GlobalStuff.AdaptyProducts = products
                                                GlobalStuff.AdaptyConfig = viewConfiguration
                                                GlobalStuff.navController.navigate(R.id.paywall_frag, Bundle())
                                                res = "OK"
                                            }

                                            is AdaptyResult.Error -> {
                                                GlobalStuff.AdaptyErr = r.error
                                                res = r.error.message!!
                                            }
                                        }
                                    }
                                }
                            }

                            is AdaptyResult.Error -> {
                                GlobalStuff.AdaptyErr = resprod.error
                                res = resprod.error.message!!
                            }
                        }
                    }
                }

                is AdaptyResult.Error -> {
                    GlobalStuff.AdaptyErr = result.error
                    res = result.error.message!!
                }
            }
        }

        return res
    }
}