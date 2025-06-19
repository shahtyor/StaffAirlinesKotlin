package com.stukalov.staffairlines.pro.ui.onboard

import android.os.Bundle
import android.util.Log
import com.adapty.Adapty
import com.adapty.models.AdaptyPlacementFetchPolicy
import com.adapty.ui.AdaptyUI
import com.adapty.utils.AdaptyResult
import com.adapty.utils.seconds
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R

class OnboardController {

    fun GetOnboardViewParams(placement_name: String) {

        Adapty.getOnboarding(placement_name) { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val onboarding = result.value

                    val paramOnboardingConfigDisable = onboarding.remoteConfig?.dataMap?.get("statusOnboarding")

                    Log.d("GetOnboardViewParams", "statusOnboarding=" + paramOnboardingConfigDisable.toString())

                    if (paramOnboardingConfigDisable == "disable")
                    {
                        GlobalStuff.navController.navigate(R.id.carousel_frag, Bundle())
                    }
                    else {
                        if (onboarding.hasViewConfiguration) {
                            val configuration = AdaptyUI.getOnboardingConfiguration(onboarding)

                            GlobalStuff.OnboardConfig = configuration
                            GlobalStuff.navController.navigate(R.id.onboard_frag, Bundle())
                        }
                    }
                }

                is AdaptyResult.Error -> {
                    GlobalStuff.AdaptyErr = result.error
                    if (!result.error.message.isNullOrEmpty()) {
                        Log.d("getOnboarding", result.error.message!!)
                    }
                }
            }
        }
    }
}