package com.stukalov.staffairlines.pro.ui.paywall

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.adapty.models.AdaptyProfile
import com.adapty.models.AdaptyPurchaseResult
import com.adapty.ui.AdaptyPaywallView
import com.adapty.ui.AdaptyUI
import com.adapty.ui.listeners.AdaptyUiDefaultEventListener
import com.adapty.ui.listeners.AdaptyUiTagResolver
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.survicate.surveys.Survicate
import com.survicate.surveys.traits.UserTrait

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
    val SM: StaffMethods = StaffMethods()

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
                SM.SendToAmpErrorPaywall(error, product)
                super.onPurchaseFailure(error, product, context)
            }

            override fun onPurchaseStarted(product: AdaptyPaywallProduct, context: Context) {
                val descProduct = product.vendorProductId + "|" + product.price.localizedString + "|" + product.subscriptionDetails?.localizedSubscriptionPeriod
                SM.SendToAmpClickPaywall(product.paywallName, descProduct, product.vendorProductId, product.subscriptionDetails?.introductoryOfferEligibility?.name!!, "typeIntroOffer", "descIntroOffer", GlobalStuff.PointOfShow!!)
                super.onPurchaseStarted(product, context)
            }

            override fun onPurchaseFinished(
                purchaseResult: AdaptyPurchaseResult,
                product: AdaptyPaywallProduct,
                context: Context
            ) {
                //super.onPurchaseFinished(purchaseResult, product, context)
                if (purchaseResult is AdaptyPurchaseResult.Success) {

                    val descProduct = product.vendorProductId + "|" + product.price.localizedString + "|" + product.subscriptionDetails?.localizedSubscriptionPeriod
                    SM.SendToAmpPurchasePaywall(product.paywallName, descProduct, product.vendorProductId, product.subscriptionDetails?.introductoryOfferEligibility?.name!!, "typeIntroOffer", "descIntroOffer", GlobalStuff.PointOfShow!!)

                    val profile = purchaseResult.profile
                    GlobalStuff.AdaptyProfileID = profile.profileId
                    val premium = profile.accessLevels["premium"]
                    val OldPremiumAccess = GlobalStuff.premiumAccess

                    if (premium?.isActive == true)
                    {
                        GlobalStuff.premiumAccess = true
                        GlobalStuff.subscriptionId = premium.vendorProductId
                        if (!OldPremiumAccess) {
                            Survicate.setUserTrait(UserTrait("paidStatus", "premiumAccess"))
                        }
                    }
                    else
                    {
                        GlobalStuff.premiumAccess = false
                        GlobalStuff.subscriptionId = null
                        if (OldPremiumAccess) {
                            Survicate.setUserTrait(UserTrait("paidStatus", "free plan"))
                        }
                    }
                    if (GlobalStuff.HF != null) {
                        GlobalStuff.HF!!.SetPlan()
                    }
                    parentFragmentManager.popBackStack()
                }
            }

            override fun onRestoreStarted(context: Context) {
                super.onRestoreStarted(context)
            }

            override fun onRestoreFailure(error: AdaptyError, context: Context) {
                SM.SendToAmpErrorRestore(error)
                super.onRestoreFailure(error, context)
            }

            override fun onRestoreSuccess(
                profile: AdaptyProfile,
                context: Context,
            ) {
                val event = GlobalStuff.GetBaseEvent("restore paywall", true, false)
                event.eventProperties = mutableMapOf("pointOfShow" to GlobalStuff.PointOfShow,
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)

                if (profile.accessLevels["premium"]?.isActive == true) {
                    parentFragmentManager.popBackStack()
                }
            }
        }

        val pInfo: PackageInfo = view.context.getPackageManager()!!.getPackageInfo(view.context.getPackageName(), 0)
        val build = pInfo.versionCode.toString() + " (" + pInfo.versionName + ")"
        val arrayProducts = arrayListOf<String>()
        GlobalStuff.AdaptyProducts.forEach { it ->
            var descIntroOffer = "/Intro: " + it.subscriptionDetails?.introductoryOfferEligibility + "|?"
            arrayProducts.add(it.vendorProductId + "|" + it.price.localizedString + "|" + it.subscriptionDetails?.localizedSubscriptionPeriod + "|" + descIntroOffer)
        }

        val event = GlobalStuff.GetBaseEvent("show paywall", false, false)
        event.userProperties = mutableMapOf("Aircompany" to GlobalStuff.OwnAC, "Build" to build)
        event.eventProperties = mutableMapOf("paywallId" to GlobalStuff.AdaptyPaywallID,
            "paywallVer" to GlobalStuff.AdaptyPaywallRev,
            "productsList" to arrayProducts.joinToString(","),
            "pointOfShow" to GlobalStuff.PointOfShow,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)

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