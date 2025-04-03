package com.stukalov.staffairlines.pro.ui.paywall

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.adapty.models.AdaptyProfile
import com.adapty.models.AdaptyPurchaseResult
import com.adapty.ui.AdaptyPaywallView
import com.adapty.ui.AdaptyUI
import com.adapty.ui.listeners.AdaptyUiDefaultEventListener
import com.adapty.ui.listeners.AdaptyUiTagResolver
import com.adapty.utils.AdaptyResult
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R

class PaywallUiFragment : Fragment(R.layout.fragment_paywall_ui) {

    /*companion object {
        fun newInstance(
            viewConfig: AdaptyUI.LocalizedViewConfiguration,
            products: List<AdaptyPaywallProduct>,
        ) =
            PaywallUiFragment().apply {
                this.products = products
                this.viewConfiguration = viewConfig
            }
    }*/

    private var viewConfiguration: AdaptyUI.LocalizedViewConfiguration? = null
    private var products = listOf<AdaptyPaywallProduct>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //GlobalStuff.setActionBar(false, false, "")
        GlobalStuff.actionBar?.hide()

        products = GlobalStuff.AdaptyProducts
        viewConfiguration = GlobalStuff.AdaptyConfig

        val paywallView = view as? AdaptyPaywallView ?: return
        //val paywallView = view.findViewById<AdaptyPaywallView>(R.id.vPaywallView)
        val viewConfig = viewConfiguration ?: return

        val eventListener = object: AdaptyUiDefaultEventListener() {

            override fun onPurchaseFailure(
                error: AdaptyError,
                product: AdaptyPaywallProduct,
                context: Context
            ) {
                super.onPurchaseFailure(error, product, context)
            }

            override fun onPurchaseFinished(
                purchaseResult: AdaptyPurchaseResult,
                product: AdaptyPaywallProduct,
                context: Context
            ) {
                //super.onPurchaseFinished(purchaseResult, product, context)
                if (purchaseResult is AdaptyPurchaseResult.Success) {
                    val profile = purchaseResult.profile
                    GlobalStuff.AdaptyProfileID = profile.profileId
                    val premium = profile.accessLevels["premium"]

                    if (premium?.isActive == true)
                    {
                        GlobalStuff.premiumAccess = true
                        GlobalStuff.subscriptionId = premium.vendorProductId
                    }
                    else
                    {
                        GlobalStuff.premiumAccess = false;
                        GlobalStuff.subscriptionId = null;
                    }
                    if (GlobalStuff.HF != null) {
                        GlobalStuff.HF!!.SetPlan()
                    }
                    parentFragmentManager.popBackStack()
                }
            }

            override fun onRestoreSuccess(
                profile: AdaptyProfile,
                context: Context,
            ) {
                if (profile.accessLevels["premium"]?.isActive == true) {
                    parentFragmentManager.popBackStack()
                }
            }
        }

        paywallView.showPaywall(
            viewConfig,
            products,
            eventListener,
            tagResolver = AdaptyUiTagResolver.DEFAULT
        )

        /**
         * Also you can get the `AdaptyPaywallView` and set paywall right away
         * by calling `AdaptyUi.getPaywallView()`
         */
    }
}