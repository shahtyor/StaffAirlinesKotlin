package com.stukalov.staffairlines.pro

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.adapty.Adapty
import com.adapty.models.AdaptyConfig
import com.adapty.models.AdaptyProfile
import com.adapty.models.AdaptyProfileParameters
import com.adapty.ui.AdaptyUI
import com.adapty.ui.onboardings.actions.AdaptyOnboardingCloseAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingCustomAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingOpenPaywallAction
import com.adapty.ui.onboardings.actions.AdaptyOnboardingStateUpdatedAction
import com.adapty.ui.onboardings.errors.AdaptyOnboardingError
import com.adapty.ui.onboardings.events.AdaptyOnboardingAnalyticsEvent
import com.adapty.ui.onboardings.listeners.AdaptyOnboardingEventListener
import com.adapty.utils.AdaptyLogLevel
import com.adapty.utils.AdaptyResult
import com.adapty.utils.ImmutableMap
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.survicate.surveys.Survicate
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import com.survicate.surveys.QuestionAnsweredEvent
import com.survicate.surveys.SurveyClosedEvent
import com.survicate.surveys.SurveyCompletedEvent
import com.survicate.surveys.SurveyDisplayedEvent
import com.survicate.surveys.SurvicateEventListener
import com.survicate.surveys.traits.UserTrait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    lateinit var signInClient: GoogleSignInClient
    lateinit var signInOptions: GoogleSignInOptions
    lateinit var adapter: BottomMenuAdapter
    val RC_SIGN_IN: Int = 1
    private lateinit var auth: FirebaseAuth
    val SM: StaffMethods = StaffMethods()

    val listener = object : SurvicateEventListener() {
        override fun onSurveyDisplayed(event: SurveyDisplayedEvent) {
            Log.d("SurveyEvent","DELEGATE survey_displayed " + event.surveyId)
            SM.sendEventSurvey("survey displayed", event.surveyId, null, null)
        }
        override fun onQuestionAnswered(event: QuestionAnsweredEvent) {
            Log.d("SurveyEvent", "DELEGATE question_answered " + event.surveyId + "/" + event.questionText + "/" + event.answer.value)
            SM.sendEventSurvey("survey question answered", event.surveyId, event.questionText, event.answer.value)
        }
        override fun onSurveyClosed(event: SurveyClosedEvent) {
            Log.d("SurveyEvent","DELEGATE survey_closed " + event.surveyId)
            SM.sendEventSurvey("survey closed", event.surveyId, null, null)
        }
        override fun onSurveyCompleted(event: SurveyCompletedEvent) {
            Log.d("SurveyEvent","DELEGATE survey_completed " + event.surveyId)
            SM.sendEventSurvey("survey completed", event.surveyId, null, null)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Adapty.logLevel = AdaptyLogLevel.VERBOSE

        val cd = ColorDrawable(Color.parseColor("#3b3b3b"))

        val actionBar = getSupportActionBar()
        actionBar?.setBackgroundDrawable(cd)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.show()

        auth = FirebaseAuth.getInstance()

        GlobalStuff.Pax = 1

        val amplitude = Amplitude(
            Configuration(
                apiKey = getString(R.string.amplitude_api_key),
                context = applicationContext,
                flushIntervalMillis = 10000,
                flushQueueSize = 5,
            )
        )

        var amplitudeDeviceId = amplitude.store.deviceId
        if (amplitudeDeviceId.isNullOrEmpty())
        {
            amplitudeDeviceId = amplitude.getDeviceId()
        }

        if (!amplitudeDeviceId.isNullOrEmpty()) {
            GlobalStuff.AdaptyAmplitudeDeviceId = amplitudeDeviceId
            Adapty.setIntegrationIdentifier("amplitude_device_id", amplitude.store.deviceId!!) { error ->
                if (error != null)
                {
                    Log.d("Adapty.setIntegrationIdentifier", error.adaptyErrorCode.name + "..." + error.message + "..." + error.originalError?.message)
                }
            }
        }
        else {
            Log.d("Adapty.setIntegrationIdentifier", "amplitudeDeviceId=NULL")
        }

        AppsFlyerLib.getInstance().init(getString(R.string.appsFlyerDevKey), ConversionLietener(), this)
        AppsFlyerLib.getInstance().waitForCustomerUserId(true)
        AppsFlyerLib.getInstance().start(this)
        Log.d("AppsFlyerLib", "Start")

        Survicate.init(this)
        if (GlobalStuff.TypeUserForSurvicate == "test") {
            Survicate.reset()
        }
        Survicate.setUserTrait(UserTrait("testORprod", GlobalStuff.TypeUserForSurvicate))

        /*Adapty.activate(
            applicationContext,
            AdaptyConfig.Builder("public_live_acbEIggW.S6BV02NUN0hqnxiqZLGS")
                .withObserverMode(true) //default false
                .withCustomerUserId(GlobalStuff.customerID) //default null
                .withIpAddressCollectionDisabled(false) //default false
                .withAdIdCollectionDisabled(false) // default false
                .build()
        )*/

        if (GlobalStuff.customerID != null)
        {
            amplitude.setUserId(GlobalStuff.customerID)
            val userprop = mutableMapOf("preset filter" to SM.GetStringStatus(GlobalStuff.UsePermitted))
            amplitude.identify(userprop)
            //AppsFlyerLib.getInstance().setCustomerUserId(GlobalStuff.customerID)
            AppsFlyerLib.getInstance().setCustomerIdAndLogSession(GlobalStuff.customerID, this)
            OneSignal.login(GlobalStuff.customerID!!)
            Survicate.setUserTrait(UserTrait("user_id", GlobalStuff.customerID))
            Log.d("AppsFlyerLib", "setCustomerUserId")
        }

        AdaptyGetProfile()

        Adapty.setOnProfileUpdatedListener{ profile ->
            GlobalStuff.AdaptyProfileID = profile.profileId
            val OldPremiumAccess = GlobalStuff.premiumAccess
            val premium = profile.accessLevels["premium"]

            Log.d("setOnProfileUpdatedListener", "premium=" + premium?.isActive.toString())

            if (premium?.isActive == true)
            {
                GlobalStuff.premiumAccess = true
                if (!OldPremiumAccess)
                {
                    Survicate.setUserTrait(UserTrait("paidStatus", "premiumAccess"))
                }
                GlobalStuff.subscriptionId = premium.vendorProductId
            }
            else
            {
                GlobalStuff.premiumAccess = false
                if (OldPremiumAccess)
                {
                    Survicate.setUserTrait(UserTrait("paidStatus", "free plan"))
                }
                GlobalStuff.subscriptionId = null
            }

            //BuildProfileToken(profile.customAttributes, premium)

            if (GlobalStuff.HF != null) {
                GlobalStuff.HF!!.SetPlan()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        /*lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) { SM.LoadLocations() }
        }

        lifecycleScope.launch {
            val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }
        }*/

        GlobalStuff.activity = this.baseContext
        GlobalStuff.mActivity = this

        GlobalStuff.navController = navController
        GlobalStuff.actionBar = actionBar
        GlobalStuff.StaffRes = resources
        GlobalStuff.ResType = ResultType.Direct
        GlobalStuff.supportFragManager = supportFragmentManager
        GlobalStuff.amplitude = amplitude
        GlobalStuff.density = this.baseContext.resources.displayMetrics.density

        setupGoogleLogin()

        /*val fireinst = FirebaseMessaging.getInstance()
        if (fireinst.token.isSuccessful)
        {
            val strt = fireinst.token.result
        }*/

        //SM.GetAppToken()

        if (GlobalStuff.Token.isNullOrEmpty())
        {
            SM.GetAppToken()
        }

        if (GlobalStuff.Token.isNullOrEmpty()) {
            lifecycleScope.launch {
                val token = withContext(Dispatchers.IO) {
                    FirebaseMessaging.getInstance().token
                }

                if (token.isSuccessful) {
                    GlobalStuff.Token = token.result
                    SM.SaveAppToken()
                }
            }
        }

        //SM.SaveVoidFavourites()
        SM.GetOwnAC()
        SM.ReadPermit()
        SM.ReadFavorites()
        SM.ReadHistory()
        SM.SetDefaultsForRating()

        if (GlobalStuff.OwnAC != null) {
            OneSignal.InAppMessages.addTrigger("os_ownAC", GlobalStuff.OwnAC!!.Code)
            OneSignal.InAppMessages.addTrigger(
                "os_presetAC",
                if (GlobalStuff.Permitted.isEmpty()) "notExists" else "exists"
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        if (GlobalStuff.Token != null) {
            lifecycleScope.launch {
                val rem = withContext(Dispatchers.IO) { SM.RemainSubscribe(GlobalStuff.Token!!) }

                if (rem != null) {
                    GlobalStuff.Remain = rem.count
                }
            }
        }

        Log.d("onCreate", "End")

        Survicate.addEventListener(listener)

        if (GlobalStuff.NotiData != null)
        {
            GlobalStuff.FirstHomeOpen = false
            OpenNotificationFlight(GlobalStuff.NotiData!!)
        }
        else
        {
            ExtrasProcess(intent.extras)
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    fun AdaptyGetProfile(initCF: Boolean = false) {
        if (GlobalStuff.aProfile != null) {
            val profile = GlobalStuff.aProfile!!
            SM.BuildProfileToken(profile.customAttributes, GlobalStuff.premiumAccess)
        }

        if (!GlobalStuff.Token.isNullOrEmpty()) {
            lifecycleScope.launch {
                val rem = withContext(Dispatchers.IO) { SM.RemainSubscribe(GlobalStuff.Token!!) }

                if (rem != null) {
                    GlobalStuff.Remain = rem.count

                    if (GlobalStuff.HF != null) {
                        GlobalStuff.HF?.SetPlan()
                    }

                    if (initCF) {
                        if (GlobalStuff.CF != null) {
                            GlobalStuff.CF?.Init(GlobalStuff.CF?.requireContext()!!)
                        }
                        GlobalStuff.navController.navigateUp()
                    }
                }
            }
        } else {
            if (GlobalStuff.HF != null) {
                GlobalStuff.HF!!.SetPlan()
            }
        }
    }

    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
        GlobalStuff.googleInClient = signInClient
    }

    private fun ExtrasProcess(extras: Bundle?) {
        Log.d("ExtrasProcess", "Start")

        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val data: NotificationData = NotificationData("", "", "", LocalDateTime.now(), 0, "", "")

            Log.d("ExtrasProcess", "Prepare")

            if (extras != null) {
                Log.d("ExtrasProcess", "extras != null")
                GlobalStuff.FirstHomeOpen = false

                extras.keySet().forEach { key ->
                    if (key != null) {
                        if (key == "type") {
                            data.type = intent.extras?.getString(key)!!
                        } else if (key == "origin") {
                            data.origin = intent.extras?.getString(key)!!
                        } else if (key == "destination") {
                            data.destination = intent.extras?.getString(key)!!
                        } else if (key == "departureDateTime") {
                            data.departureDateTime =
                                LocalDateTime.parse(intent.extras?.getString(key)!!, formatter)
                        } else if (key == "paxAmount") {
                            data.paxAmount = intent.extras?.getString(key)!!.toInt()
                        } else if (key == "marketingCarrier") {
                            data.marketingCarrier = intent.extras?.getString(key)!!
                        } else if (key == "flightNumber") {
                            data.flightNumber = intent.extras?.getString(key)!!
                        }
                    }
                }

                Log.d("ExtrasProcess", "End")

                OpenNotificationFlight(data)
            }
            else {
                Log.d("ExtrasProcess", "Null")
            }

        } catch (ex: Exception) {
            Log.d("ExtrasProcess", ex.message + "..." + ex.stackTrace)
        }
    }

    fun OpenNotificationFlight(data: NotificationData)
    {
        Log.d("OpenNotificationFlight", "Start")

        if (data.paxAmount == 0)
        {
            data.paxAmount = 1;
        }

        lifecycleScope.launch {
            val res = withContext(Dispatchers.IO) {
                SM.GetFlightInfo(
                    data.origin,
                    data.destination,
                    data.departureDateTime,
                    data.paxAmount,
                    data.marketingCarrier,
                    data.flightNumber,
                    GlobalStuff.Token,
                    GlobalStuff.customerID
                )
            }

            Log.d("OpenNotificationFlight", "OK")

            if (res == "OK" && GlobalStuff.FlInfo != null) {
                if (GlobalStuff.FlInfo?.Flight != null) {
                    Log.d(
                        "OpenNotificationFlight",
                        "Step 1. " + GlobalStuff.FlInfo?.Flight?.FlightNumber!!
                    )

                    GlobalStuff.OneResult = GlobalStuff.FlInfo?.Flight
                    SM.SaveAppToken()
                    Log.d(
                        "OpenNotificationFlight",
                        "Step 2. " + GlobalStuff.OneResult?.FlightNumber!!
                    )
                    GlobalStuff.NotiData = null

                    GlobalStuff.navController.navigate(R.id.result_one)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //val user = FirebaseAuth.getInstance().currentUser
        //if (user != null) {
            //startActivity(LoggedInActivity.getLaunchIntent(this))
            //finish()
        //}
    }

    override fun onDestroy() {
        Survicate.removeEventListener(listener)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN || 1==1) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleFirebaseAuth(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    /*private fun checkAndRequestMissingPermissions(context: Context) {
        // check required permissions - request those which have not already been granted
        val missingPermissionsToBeRequested = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission) != PackageManager.PERMISSION_GRANTED)
            missingPermissionsToBeRequested.add(Manifest.permission.BLUETOOTH)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
            missingPermissionsToBeRequested.add(Manifest.permission.BLUETOOTH_ADMIN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 and above require both BLUETOOTH_CONNECT and BLUETOOTH_SCAN
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                missingPermissionsToBeRequested.add(Manifest.permission.BLUETOOTH_CONNECT)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                missingPermissionsToBeRequested.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // FINE_LOCATION is needed for Android 10 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                missingPermissionsToBeRequested.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                missingPermissionsToBeRequested.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (missingPermissionsToBeRequested.isNotEmpty()) {
            writeToLog("Missing the following permissions: $missingPermissionsToBeRequested")
            ActivityCompat.requestPermissions(this, missingPermissionsToBeRequested.toArray(arrayOfNulls<String>(0)), REQUEST_MULTIPLE_PERMISSIONS)
        } else {
            writeToLog("All required permissions GRANTED !")
        }
    }*/

    /*fun myOnBoardingShow()
    {
        Adapty.getOnboarding("onOpenOnboarding") { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val onboarding = result.value

                    val paramOnboardingConfigDisable = onboarding.remoteConfig?.dataMap?.get("statusOnboarding")

                    if (paramOnboardingConfigDisable == "disable")
                    {
                        val configuration =  AdaptyUI.getOnboardingConfiguration(onboarding)

                        val view = AdaptyUI.getOnboardingView(this, configuration, eventListener)
                        view.show(configuration, this.eventListener)
                    }
                    else
                    {
                        val configuration =  AdaptyUI.getOnboardingConfiguration(onboarding)

                        AdaptyUI.getOnboardingView(this, configuration, eventListener)
                    }
                }
                is AdaptyResult.Error -> {
                    val error = result.error
                    // handle the error
                }
            }
        }
    }*/

    private fun googleFirebaseAuth(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val cMD5 = "2_" + md5(acct.id!!)
                GlobalStuff.customerID = cMD5
                GlobalStuff.customerEmail = acct.email
                GlobalStuff.customerFirstName = acct.givenName
                GlobalStuff.customerLastName = acct.familyName
                AppsFlyerLib.getInstance().setCustomerUserId(cMD5)
                GlobalStuff.amplitude?.setUserId(cMD5)

                val event = GlobalStuff.GetBaseEvent("login", true, false)
                event.eventProperties = mutableMapOf("type_system" to "google",
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event)

                Survicate.setUserTrait(UserTrait("user_id", GlobalStuff.customerID))
                val traits = listOf(
                    UserTrait("first_name", acct.givenName),
                    UserTrait("last_name", acct.familyName),
                    UserTrait("email", acct.email)
                )
                Survicate.setUserTraits(traits)

                SM.SaveCustomerID()
                OneSignal.login(cMD5)
                GlobalStuff.SaveOneSignalToAdapty()

                Adapty.identify(cMD5) { error ->
                    if (error == null) {

                        val aprof = AdaptyProfileParameters.Builder()
                            .withEmail(acct.email)
                            .withFirstName(acct.givenName)
                            .withLastName(acct.familyName)
                            .withCustomAttribute("own_ac", GlobalStuff.OwnAC?.Code!!)

                        Adapty.updateProfile(aprof.build()) { error2 ->
                            if (error2 != null) {
                                Toast.makeText(GlobalStuff.activity, "Update profile failed " + error2.message, Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                val p1 = GlobalStuff.premiumAccess

                                AdaptyGetProfile(true)

                                val p2 = GlobalStuff.premiumAccess

                                /*if (GlobalStuff.CF != null) {
                                    GlobalStuff.CF?.Init()
                                }
                                GlobalStuff.navController.navigateUp()*/
                            }
                        }
                    }
                }

                Toast.makeText(GlobalStuff.activity, "Google sign in success " + acct.displayName, Toast.LENGTH_LONG).show()
                //startActivity(LoggedInActivity.getLaunchIntent(GlobalStuff.activity))
            } else {
                Toast.makeText(GlobalStuff.activity, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}