package com.stukalov.staffairlines.pro

import android.app.Activity
import android.content.Context
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.adapty.models.AdaptyProfile
import com.adapty.models.AdaptyPurchaseResult
import com.adapty.models.AdaptySubscriptionUpdateParameters
import com.adapty.ui.AdaptyUI
import com.adapty.ui.listeners.AdaptyUiDefaultEventListener
import com.adapty.ui.listeners.AdaptyUiEventListener

public class AdaptyListener : AdaptyUiDefaultEventListener() {

/*    override fun onActionPerformed(action: AdaptyUI.Action, context: Context) {
        when (action) {
            AdaptyUI.Action.Close -> (context as? Activity)?.onBackPressed()

            is AdaptyUI.Action.OpenUrl -> {}

            is AdaptyUI.Action.Custom -> {}
        }
    }

    public override fun onProductSelected(
        product: AdaptyPaywallProduct,
        context: Context,
    ) {
        val i=1
    }

    public override fun onPurchaseStarted(
        product: AdaptyPaywallProduct,
        context: Context,
    ) {
        val k=3
    }

    public override fun onPurchaseFinished(
        purchaseResult: AdaptyPurchaseResult,
        product: AdaptyPaywallProduct,
        context: Context,
    ) {
        if (purchaseResult !is AdaptyPurchaseResult.UserCanceled)
            (context as? Activity)?.onBackPressed()
    }

    public override fun onPurchaseFailure(
        error: AdaptyError,
        product: AdaptyPaywallProduct,
        context: Context,
    ) {
        val j=5
    }

    public override fun onRestoreSuccess(
        profile: AdaptyProfile,
        context: Context,
    ) {
        val h=8
    }

    public override fun onRestoreFailure(
        error: AdaptyError,
        context: Context,
    ) {
        val f=0
    }

    public override fun onAwaitingSubscriptionUpdateParams(
        product: AdaptyPaywallProduct,
        context: Context,
        onSubscriptionUpdateParamsReceived: AdaptyUiEventListener.SubscriptionUpdateParamsCallback,
    ) {
        onSubscriptionUpdateParamsReceived(AdaptySubscriptionUpdateParameters("", AdaptySubscriptionUpdateParameters.ReplacementMode.CHARGE_FULL_PRICE))
    }

    public override fun onLoadingProductsFailure(
        error: AdaptyError,
        context: Context,
    ): Boolean = false

    public override fun onRenderingError(
        error: AdaptyError,
        context: Context,
    ) {
        val t=6
    }

    override fun onRestoreStarted(context: Context) {
        super.onRestoreStarted(context)
    }*/
}