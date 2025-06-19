package com.stukalov.staffairlines.pro.ui.onboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.adapty.ui.onboardings.AdaptyOnboardingMetaParams
import com.adapty.ui.onboardings.AdaptyOnboardingView
import com.adapty.ui.onboardings.actions.AdaptyOnboardingCloseAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingCustomAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingOpenPaywallAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingStateUpdatedAction
import com.adapty.ui.onboardings.errors.AdaptyOnboardingError
import com.adapty.ui.onboardings.events.AdaptyOnboardingAnalyticsEvent
import com.adapty.ui.onboardings.listeners.AdaptyOnboardingEventListener
import com.google.gson.Gson
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R

class OnboardUiFragment : Fragment(R.layout.fragment_onboard_ui) {

    private val eventListener = object : AdaptyOnboardingEventListener {
        override fun onAnalyticsEvent(event: AdaptyOnboardingAnalyticsEvent, context: Context) {
            Log.d("ListenerOnboard", event.toString())
            when (event) {
                is AdaptyOnboardingAnalyticsEvent.OnboardingStarted -> {
                    // Track onboarding start
                    sendEventOnboardingAdapty("onboarding_started", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.ScreenPresented -> {
                    // Track screen presentation
                    sendEventOnboardingAdapty("screen_presented", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.ScreenCompleted -> {
                    // Track screen completion with user response
                    sendEventOnboardingAdapty("screen_completed", event.meta, event.elementId, event.reply)
                }
                is AdaptyOnboardingAnalyticsEvent.OnboardingCompleted -> {
                    // Track successful onboarding completion
                    sendEventOnboardingAdapty("onboarding_completed", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.Unknown -> {
                    // Handle unknown events
                    sendEventOnboardingAdapty(event.name, event.meta)
                }
                // Handle other cases as needed
                is AdaptyOnboardingAnalyticsEvent.ProductsScreenPresented -> {
                    //sendEventOnboardingAdapty("products_screen_presented", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.RegistrationScreenPresented -> {
                    //sendEventOnboardingAdapty("registration_screen_presented", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.SecondScreenPresented -> {
                    //sendEventOnboardingAdapty("second_screen_presented", event.meta)
                }
                is AdaptyOnboardingAnalyticsEvent.UserEmailCollected -> {
                    //sendEventOnboardingAdapty("user_email_collected", event.meta)
                }
            }
        }

        override fun onCloseAction(action: AdaptyOnboardingCloseAction, context: Context) {
            Log.d("ListenerOnboard", "onCloseAction")
            GlobalStuff.actionBar?.show()
            GlobalStuff.navController.popBackStack(R.id.main_frag, true)
        }

        override fun onCustomAction(action: AdaptyOnboardingCustomAction, context: Context) {
            Log.d("ListenerOnboard", action.actionId)
            when (action.actionId) {
                "allowNotifications" -> {
                    // Request notification permissions
                }
            }
        }

        override fun onError(error: AdaptyOnboardingError, context: Context) {
            Log.d("ListenerOnboard", error.toString() + "..." + error.hashCode())
        }

        override fun onFinishLoading(context: Context) {
            Log.d("Listener", "onFinishLoading")
        }

        override fun onOpenPaywallAction(
            action: AdaptyOnboardingOpenPaywallAction,
            context: Context
        ) {
            Log.d("Listener", "onOpenPaywallAction")
        }

        override fun onStateUpdatedAction(
            action: AdaptyOnboardingStateUpdatedAction,
            context: Context
        ) {
            Log.d("Listener", action.toString())
        }
    }

    fun sendEventOnboardingAdapty(etype: String, meta: AdaptyOnboardingMetaParams, elementId: String? = "", reply: String? = "")  {
        val event = GlobalStuff.GetBaseEvent(etype, false, false)
        event.eventProperties = mutableMapOf("onboardingId" to meta.onboardingId,
            "screenClientId" to meta.screenClientId,
            "screenIndex" to meta.screenIndex,
            "screensTotal" to meta.totalScreens,
            "elementId" to elementId,
            "reply" to reply
        )
        GlobalStuff.amplitude?.track(event)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalStuff.actionBar?.hide()
        //GlobalStuff.setActionBar(false, false, "")

        val viewConfiguration = GlobalStuff.OnboardConfig ?: return

        val gson = Gson()

        val onboardView = view as? AdaptyOnboardingView ?: return
        //val onboardView = view.findViewById<AdaptyOnboardingView>(R.id.vOnboardView)
        //val onboardView = AdaptyOnboardingView(GlobalStuff.mActivity)

        val sConfig = gson.toJson(viewConfiguration)

        Log.d("onViewCreatedOnboard", "viewConfig=" + sConfig)

        Log.d("onViewCreatedOnboard", "onboardView.show")

        onboardView.show(viewConfiguration, eventListener)

        Log.d("onViewCreatedOnboard", "after show")
    }
}