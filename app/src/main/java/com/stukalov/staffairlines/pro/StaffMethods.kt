package com.stukalov.staffairlines.pro

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onesignal.OneSignal
import com.survicate.surveys.Survicate
import okhttp3.ConnectionSpec
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.TimeUnit

class StaffMethods {

    //val BaseUrl: String = "https://api.staffairlines.com:8033/api"
    val BaseUrl: String = "http://dev-api.staffairlines.com:8033/api"
    var StaffApp = StaffAirlines()
    val gson = Gson()
    val client = OkHttpClient().newBuilder().connectionSpecs(
        Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS)).addInterceptor { chain ->
        val originalRequest = chain.request()

        val builder = originalRequest.newBuilder()
            .header("Authorization", Credentials.basic("sae2", "ISTbetweenVAR1999"))
        val newRequest = builder.build()
        chain.proceed(newRequest)
        }.connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .build()

    fun ExtendedSearch(origin: String, destination: String, date: LocalDate, list: String, GetTransfer: Boolean, ntype: GetNonDirectType, pax: Int, currency: String, lang: String, country: String, token: String = "void token", sa: Boolean = true, ver: String = "1.0", ac: String = "--", id_user: String? = "common ticketapi user"): String
    {
        var res: String = ""
        try
        {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val Uri = BaseUrl + "/amadeus/ExtendedSearch?origin=" + origin + "&destination=" + destination + "&date=" + date.format(formatter) + "&list=" + list + "&GetTransfer=" + GetTransfer.toString() + "&GetNonDirect=" + ntype.toString() + "&pax=" + pax + "&token=" + token + "&sa=" + sa.toString() + "&ver=" + ver + "&ac=" + ac + "&currency=" + currency + "&lang=" + lang + "&country=" + country + "&id_user" + id_user
            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val ExtData: ExtendedResult
            val gson = Gson()

            try {
                ExtData = gson.fromJson(Json, ExtendedResult::class.java)
                GlobalStuff.ExtResult = ExtData

                return "OK"
            }
            catch (e: Exception)
            {
                res = e.message + "..." + e.stackTrace
            }
        }
        catch (ex: Exception)
        {
            res = ex.message + "..." + ex.stackTrace
        }
        return res
    }

    fun GetFlightInfo(origin: String, destination: String, date: LocalDateTime, pax: Int, aircompany: String, number: String, token: String? = "void token", id_user: String? = "common ticketapi user"): String
    {
        var res: String = ""
        try
        {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatterLong = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val strdate = date.format(formatter)
            val strnow = LocalDateTime.now().format(formatterLong)
            val Uri: String = BaseUrl + "/amadeus/GetFlightInfo?origin=" + origin + "&destination=" + destination + "&date=" + strdate + "&pax=" + pax + "&aircompany=" + aircompany + "&number=" + number + "&now=" + strnow + "&token=" + token + "&id_user=" + id_user;
            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val FlightData: FlightInfo
            val gson = Gson()

            try
            {
                FlightData = gson.fromJson(Json, FlightInfo::class.java)
                GlobalStuff.FlInfo = FlightData

                return "OK"
            }
            catch (e: Exception)
            {
                res = e.message + "..." + e.stackTrace
            }
        }
        catch (ex: Exception)
        {
            res = ex.message + "..." + ex.stackTrace
        }
        return res
    }

    fun GetNonDirectFlights(origin: String, destination: String, change: String, date: LocalDate, list: String, pax: Int, currency: String, lang: String, token: String? = "void token", ver: String = "1.0", id_user: String? = "common ticketapi user"): String
    {
        var res: String = ""
        try
        {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val Uri: String = BaseUrl + "/amadeus/GetNonDirectFlights?origin=" + origin + "&destination=" + destination + "&change=" + change + "&date=" + date.format(formatter) + "&list=" + list + "&pax=" + pax + "&token=" + token + "&ver=" + ver + "&currency=" + currency + "&lang=" + lang + "&id_user=" + id_user;

            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val nonDirect: NonDirectResult
            val gson = Gson()

            try
            {
                nonDirect = gson.fromJson(Json, NonDirectResult::class.java)

                val ExtRes = GlobalStuff.ExtResult
                if (ExtRes != null)
                {
                    var NonDirRes: MutableList<NonDirectResult> = mutableListOf()
                    if (ExtRes.NonDirectRes != null && ExtRes.NonDirectRes.isNotEmpty())
                    {
                        NonDirRes = ExtRes.NonDirectRes.toMutableList()
                    }

                    var NonRes: NonDirectResult? = null
                    var NonResList = NonDirRes.filter { it -> it.Transfer == change }
                    if (NonResList.isNotEmpty())
                    {
                        NonRes = NonResList[0]
                    }

                    if (NonRes == null)
                    {
                        NonDirRes.add(0, nonDirect)
                    }
                    else
                    {
                        NonRes = nonDirect
                    }

                    ExtRes.NonDirectRes = NonDirRes.toList()
                }

                return "OK"
            }
            catch (e: Exception)
            {
                res = e.message + "..." + e.stackTrace
            }
        }
        catch (ex: Exception)
        {
            res = ex.message + "..." + ex.stackTrace
        }
        return res
    }

    fun RemainSubscribe(token: String): Remain?
    {
        try
        {
            val Uri = BaseUrl + "/subscribes/Remain?token=" + token

            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            var rem: Remain? = null
            val gson = Gson()

            try {
                rem = gson.fromJson(Json, Remain::class.java)
            }
            catch (e: Exception)
            {
                val kjh = e.message + "..." + e.stackTrace
            }
            return rem
        }
        catch (ex: Exception)
        {
            val kjh = ex.message + "..." + ex.stackTrace
        }
        return null
    }

    fun GenToken(id_user: String): Token?
    {
        try
        {
            val tmp = id_user.split('_')
            val Uri = BaseUrl + "/token/Gen?type=" + tmp[0] + "&id_user=" + tmp[1]
            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)
            var tok: Token? = null

            try
            {
                tok = gson.fromJson(Json, Token::class.java)
            } catch (e: Exception)
            {
                val hk = "kljl"
            }
            return tok
        }
        catch (ex: Exception)
        {
            val kjh = "sdfds"
        }
        return null
    }

    fun CreateSubscribe(token: String, flight: String, origin: String, destination: String, pax: Int, time: String): SubscribeWithRemainResult
    {
        try
        {
            val Uri: String = BaseUrl + "/subscribes/Create?token=" + token + "&flight=" + flight + "&origin=" + origin + "&destination=" + destination + "&pax=" + pax.toString() + "&time=" + time.replace("T", "%20") + "&channel=1"

            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val subscribe: SubscribeResult
            val gson = Gson()

            try {
                subscribe = gson.fromJson(Json, SubscribeResult::class.java)

                val rem: Remain? = RemainSubscribe(token)

                val result2: SubscribeWithRemainResult = SubscribeWithRemainResult(subscribe.before_push, subscribe.alert, rem!!.count)

                return result2
            }
            catch (e: Exception)
            {
                val kjh = e.message + "..." + e.stackTrace
            }
        }
        catch (ex: Exception)
        {
            val kjgh = ex.message + "..." + ex.stackTrace
        }
        return SubscribeWithRemainResult(0, "", 0)
    }

    fun VoidProfile(id_user: String): VoidResult
    {
        try
        {
            val Uri: String = BaseUrl + "/token/Void?id_user=" + id_user

            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val result: VoidResult
            val gson = Gson()

            try
            {
                result = gson.fromJson(Json, VoidResult::class.java)

                return result
            }
            catch (e: Exception)
            {
                Log.d("Error", e.message + "..." + e.stackTrace)
            }
        }
        catch (ex: Exception)
        {
            Log.d("VoidProfile", ex.message + "..." + ex.stackTrace)
        }
        return VoidResult(false, "")
    }

    fun SendReportRequest(id_user: String, device_id: String, origin: String, destination: String, operating: String, flight: String, departure: LocalDateTime, pax: Int): ReportRequestStatus?
    {
        try
        {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

            val Uri: String = BaseUrl + "/token/ReportRequest?id_user=" + id_user + "&device_id=" + device_id + "&origin=" + origin + "&destination=" + destination + "&operating=" + operating + "&flight=" + flight + "&time=" + departure.format(formatter) + "&pax=" + pax

            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val result: ReportRequestStatus
            val gson = Gson()

            try {
                result = gson.fromJson(Json, ReportRequestStatus::class.java)

                return result
            }
            catch (e: Exception)
            {
                val kjh = e.message + "..." + e.stackTrace
            }
        }
        catch (ex: Exception)
        {
            val khj="123"
        }
        return null
    }

    fun LoadLocations(): String {

        var Json: String = ""

        val request = Request.Builder()
            .url(BaseUrl + "/location?lang=en")
            .build()

        Json = RequestJson(client, request)

        //val builder = GsonBuilder()
        //val gson = builder.create()
        val LocData: Array<Location>
        val gson = Gson()
        try {
            LocData = gson.fromJson(Json, Array<Location>::class.java)
            //StaffApp.Locations = LocData.toList()

            GlobalStuff.Locations = LocData.toList()
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
        return Json
    }

    fun LoadAirlines(): String
    {
        var Json: String = ""

        val request = Request.Builder()
            .url(BaseUrl + "/airlines?lang=en")
            .build()

        Json = RequestJson(client, request)

        val AirData: Array<Airline0>
        val gson = Gson()
        try {
            AirData = gson.fromJson(Json, Array<Airline0>::class.java)
            GlobalStuff.Airlines = AirData.toList()
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
        return Json
    }

    fun GetPermittedAC(code: String): String
    {
        var Json: String = ""

        val request = Request.Builder()
            .url(BaseUrl + "/permitted_ac?code=" + code)
            .build()

        Json = RequestJson(client, request)

        val AirData: Array<PermittedAC>
        val gson = Gson()
        try {
            AirData = gson.fromJson(Json, Array<PermittedAC>::class.java)

            var tmp: MutableList<PermittedAC> = mutableListOf()
            AirData.forEach { it0 ->
                val air = GlobalStuff.Airlines.filter { it -> it.Code == it0.Permit }
                if (air.size >= 1) {
                    val air0 = air.first()
                    val perm = PermittedAC(air0.Code, air0.Airline)
                    tmp.add(0, perm)
                }
            }

            GlobalStuff.Permitted = tmp.toList()

            if (GlobalStuff.Permitted.isEmpty()) {
                OneSignal.InAppMessages.addTrigger("os_presetAC", "notExists")
            } else {
                OneSignal.InAppMessages.addTrigger("os_presetAC", "exists")
            }

            SavePermitted()
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
        return Json
    }

    fun GetCurrentTimeIATA(code: String): CurDateTime
    {
        var Json: String = ""

        val request = Request.Builder()
            .url(BaseUrl + "/amadeus/GetCurrentTimeIATA?iata=" + code)
            .build()

        Json = RequestJson(client, request)

        var TimeData: CurDateTime = CurDateTime(LocalDateTime.now(), LocalDateTime.now())
        val gson = Gson()
        try {
            val TimeDataString = gson.fromJson(Json, CurDateTimeString::class.java)
            val dt1 = LocalDateTime.parse(TimeDataString.Time.split("+")[0])
            val dt2 = LocalDateTime.parse(TimeDataString.TimeServer.split("+")[0])
            TimeData = CurDateTime(dt1, dt2)
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
        return TimeData
    }

    fun RequestJson(client: OkHttpClient, request: Request): String {
        var Json: String = ""

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}"
                    )
                }
                // вывод тела ответа
                Json = response.body!!.string()
            }
        } catch (e0: Exception) {
            val stre = e0.message + "..." + e0.stackTraceToString()
        } catch (e: IOException) {
            println("Ошибка подключения: $e");
        }

        return Json
    }

    fun GetFavouritesString(): String
    {
        val astr = mutableListOf<String>()

        GlobalStuff.FavoriteList.forEach {
            if (Duration.between(LocalDateTime.now(), it.Fl.DepDateTime).toHours() <= 24) {
                val json = gson.toJson(it) //withDefaults.encodeToString(it)
                astr.add(0, json)
            }
        }
        val result = astr.joinToString("|")
        return result
    }

    fun SaveFavorites()
    {
        val OneStr = GetFavouritesString()
        val editor = GlobalStuff.prefs.edit()
        editor.putString("favourites", OneStr).apply()
    }

    fun SaveVoidFavourites()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.remove("favourites").apply()
    }

    fun ReadFavorites()
    {
        val FlightType = object : TypeToken<FlightWithPax>() {}.type

        GlobalStuff.FavoriteList.clear()

        if (GlobalStuff.prefs.contains("favourites")) {
            val result = GlobalStuff.prefs.getString("favourites", null)
            val arrres = result!!.split("|")
            arrres.forEach {
                val Fl = gson.fromJson<FlightWithPax>(it, FlightType)
                if (Fl != null) {
                    val dura = Duration.between(Fl.Fl.DepDateTime, LocalDateTime.now()).toHours()
                    if (dura <= 24) {

                        GlobalStuff.FavoriteList.add(0, Fl)
                    }
                }
            }
        }
    }

    fun ExistInFavourites(F: Flight): Boolean
    {
        var result = false
        val filt = GlobalStuff.FavoriteList.filter { it.Fl.DepartureDateTime == F.DepartureDateTime && it.Fl.FlightNumber == F.FlightNumber && it.Fl.MarketingCarrier == F.MarketingCarrier }
        if (filt.size > 0) result = true
        return result
    }

    fun SaveOwnAC()
    {
        val ac = GlobalStuff.OwnAC
        val json = gson.toJson(ac)
        val editor = GlobalStuff.prefs.edit()
        editor.putString("ownac", json).apply()
    }

    fun GetOwnAC()
    {
        val AirlineType = object : TypeToken<Airline0>() {}.type
        if (GlobalStuff.prefs.contains("ownac")) {
            val json = GlobalStuff.prefs.getString("ownac", null)
            val Air = gson.fromJson<Airline0>(json, AirlineType)
            if (Air != null) {
                    GlobalStuff.OwnAC = Air
            }
        }
    }

    fun GetGUID(): String
    {
        var did: String? = ""
        if (GlobalStuff.prefs.contains("deviceid")) {
            did = GlobalStuff.prefs.getString("deviceid", null)
        }

        if (did.isNullOrEmpty())
        {
            val id = UUID.randomUUID()
            did = id.toString()

            val editor = GlobalStuff.prefs.edit()
            editor.putString("deviceid", did).apply()
        }

        return did
    }


    fun SaveAppToken()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("app_token", GlobalStuff.Token).apply()
    }

    fun GetAppToken()
    {
        if (GlobalStuff.prefs.contains("app_token")) {
            GlobalStuff.Token = GlobalStuff.prefs.getString("app_token", null)
        }
    }

    fun SaveUsePermitted()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("use_permitted", GlobalStuff.UsePermitted.toString()).apply()
    }

    fun GetUsePermitted()
    {
        if (GlobalStuff.prefs.contains("use_permitted")) {
            GlobalStuff.UsePermitted = GlobalStuff.prefs.getString("use_permitted", null).toBoolean()
        }
    }

    fun SaveCustomerID()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("customer_id", GlobalStuff.customerID).apply()
    }

    fun GetCustomerID()
    {
        if (GlobalStuff.prefs.contains("customer_id")) {
            GlobalStuff.customerID = GlobalStuff.prefs.getString("customer_id", null)
        }
    }

    fun SaveFeatureData()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("featureUsedFilterPreset", GlobalStuff.featureUsedFilterPreset.toString()).apply()
        editor.putString("featureUsedSchedule", GlobalStuff.featureUsedSchedule.toString()).apply()
    }

    fun GetFeatureData()
    {
        if (GlobalStuff.prefs.contains("featureUsedFilterPreset")) {
            GlobalStuff.featureUsedFilterPreset = GlobalStuff.prefs.getString("featureUsedFilterPreset", "0")!!.toInt()
        }
        if (GlobalStuff.prefs.contains("featureUsedSchedule")) {
            GlobalStuff.featureUsedSchedule = GlobalStuff.prefs.getString("featureUsedSchedule", "0")!!.toInt()
        }
    }

    fun GetPermittedString(): String
    {
        val astr = mutableListOf<String>()

        GlobalStuff.Permitted.forEach {
            val json = gson.toJson(it)
            astr.add(0, json)
        }
        val result = astr.joinToString("|")
        return result
    }

    fun SavePermitted()
    {
        val OneStr = GetPermittedString()
        val editor = GlobalStuff.prefs.edit()
        editor.putString("permitted", OneStr).apply()
    }

    fun ReadPermit()
    {
        val PermittedType = object : TypeToken<PermittedAC>() {}.type

        var tmp: MutableList<PermittedAC> = mutableListOf()

        if (GlobalStuff.prefs.contains("permitted")) {
            val result = GlobalStuff.prefs.getString("permitted", null)
            val arrres = result!!.split("|")
            arrres.forEach {
                val Perm = gson.fromJson<PermittedAC>(it, PermittedType)
                if (Perm != null) {
                    tmp.add(0, Perm)
                }
            }

            GlobalStuff.Permitted = tmp.toList()
        }
    }

    fun GetStringPermitt(): String
    {
        var res = GlobalStuff.Permitted.joinToString ("-") { it.Code }
        if (!GlobalStuff.UsePermitted)
        {
            res = ""
        }
        return res
    }

    fun GetHistoryString(): String
    {
        val astr = mutableListOf<String>()

        GlobalStuff.HistoryList.forEach {
            val json = gson.toJson(it)
            astr.add(0, json)
        }
        val result = astr.joinToString("|")
        return result
    }

    fun SaveHistory()
    {
        val OneStr = GetHistoryString()
        val editor = GlobalStuff.prefs.edit()
        editor.putString("history", OneStr).apply()
    }

    fun SaveVoidHistory()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.remove("history").apply()
    }

    fun ReadHistory()
    {
        val HistoryType = object : TypeToken<HistoryElement>() {}.type

        GlobalStuff.HistoryList.clear()

        if (GlobalStuff.prefs.contains("history")) {
            val result = GlobalStuff.prefs.getString("history", null)
            val arrres = result!!.split("|")
            arrres.forEach {
                val HE = gson.fromJson<HistoryElement>(it, HistoryType)
                if (HE != null) {
                    GlobalStuff.HistoryList.add(0, HE)
                }
            }
        }
    }

    fun ExistInHistory(H: HistoryElement): Boolean
    {
        var result = false
        val filt = GlobalStuff.HistoryList.filter { it.OriginId == H.OriginId && it.DestinationId == H.DestinationId && it.SearchDate == H.SearchDate && it.Pax ==  H.Pax }
        if (filt.size > 0) result = true
        return result
    }

    fun SendToAmpClickPaywall(paywallId: String, product: String, productId: String, introOffer: String, typeIntroOffer: String, descIntroOffer: String, pointOfShow: String)
    {
        val event = GlobalStuff.GetBaseEvent("click paywall", true, false)
        event.eventProperties = mutableMapOf("paywallId" to paywallId,
                "product" to product,
                "productId" to productId,
                "introOffer" to introOffer,
                "typeIntroOffer" to typeIntroOffer,
                "descIntroOffer" to descIntroOffer,
                "pointOfShow" to pointOfShow,
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)
    }

    fun SendToAmpPurchasePaywall(paywallId: String, product: String, productId: String, introOffer: String, typeIntroOffer: String, descIntroOffer: String, pointOfShow: String)
    {
        val event = GlobalStuff.GetBaseEvent("purchase paywall", true, false)
        event.eventProperties = mutableMapOf("paywallId" to paywallId,
            "product" to product,
            "productId" to productId,
            "introOffer" to introOffer,
            "typeIntroOffer" to typeIntroOffer,
            "descIntroOffer" to descIntroOffer,
            "pointOfShow" to pointOfShow,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)
    }

    fun SendToAmpErrorPaywall(error: AdaptyError, product: AdaptyPaywallProduct) {
        val descProduct =
            product.vendorProductId + "|" + product.price.localizedString + "|" + product.subscriptionDetails?.localizedSubscriptionPeriod

        val event = GlobalStuff.GetBaseEvent("purchase error paywall", true, false)
        event.eventProperties = mutableMapOf(
            "paywallId" to product.paywallName,
            "product" to descProduct,
            "productId" to product.vendorProductId,
            "introOffer" to product.subscriptionDetails?.introductoryOfferEligibility,
            "typeIntroOffer" to "typeIntroOffer",
            "descIntroOffer" to "descIntroOffer",
            "Message" to error.message,
            "Source" to error.toString(),
            "AdaptyErrorCode" to error.adaptyErrorCode.toString(),
            "StackTrace" to error.stackTrace.toString(),
            "pointOfShow" to GlobalStuff.PointOfShow,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)
    }

    fun SendToAmpErrorRestore(error: AdaptyError)
    {
        val event = GlobalStuff.GetBaseEvent("restore error", true, false)
        event.eventProperties = mutableMapOf("Message" to error.message,
            "Source" to error.toString(),
            "AdaptyErrorCode" to error.adaptyErrorCode.toString(),
            "StackTrace" to error.stackTrace.toString(),
            "pointOfShow" to GlobalStuff.PointOfShow,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)
    }

    fun SendEventClickAgentRequest(ac: String, estimateSeats: String, classesList: String, estimateStatus: String, timeDepart: String, isAgentData: Boolean, forecastStatus: String, ageDataFromAgent: Int?)
    {
        val event = GlobalStuff.GetBaseEvent("click request agent", true, false)
        event.eventProperties = mutableMapOf("ac" to ac,
            "estimateSeats" to estimateSeats,
            "classesList" to classesList,
            "estimateStatus" to estimateStatus,
            "now" to LocalDateTime.now(),
            "timeDepart" to timeDepart,
            "forecastStatus" to forecastStatus,
            "isAgentData" to isAgentData,
            "ageDataFromAgent" to ageDataFromAgent,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)
    }

    fun GetStringStatus(status: Boolean): String
    {
        if (status) {
            return "on"
        } else {
            return "off"
        }
    }

    fun GetStringPoint(point: Int): String
    {
        when (point) {
            0 -> return "direct"
            1 -> return "transfer"
            else -> return "schedule"
        }
    }

    fun GetStringExistPreset(): String
    {
        if (GlobalStuff.Permitted.isEmpty())
        {
            return "dont exist"
        }
        else
        {
            return "exist"
        }
    }

    fun sendEventSwitcherPresetFilter(newsStatus: Boolean, point: Int)
    {
        val event = GlobalStuff.GetBaseEvent("switcher preset filter change", true, false)
        event.eventProperties = mutableMapOf("newsStatus" to GetStringStatus(newsStatus),
            "ownAC" to GlobalStuff.OwnAC?.Code,
            "point" to GetStringPoint(point))
        GlobalStuff.amplitude?.track(event)
    }

    fun sendEventClickPermittedAirlines()
    {
        val event = GlobalStuff.GetBaseEvent("click permitted airlines", true, false)
        event.eventProperties = mutableMapOf("statusPresetFilter" to GetStringStatus(GlobalStuff.UsePermitted),
            "existPreset" to GetStringExistPreset(),
            "ownAC" to GlobalStuff.OwnAC?.Code)
        GlobalStuff.amplitude?.track(event)
    }

    // события, связанные с опросами
    fun sendEventSurvey(typeEvent: String, surveyId: String, question: String?, answerValue: String?)
    {
        val event = GlobalStuff.GetBaseEvent(typeEvent, false, false)
        event.eventProperties = mutableMapOf("surveyId" to surveyId,
            "question" to if (question.isNullOrEmpty()) "" else question,
            "answerValue" to if (answerValue.isNullOrEmpty()) "" else answerValue)
        GlobalStuff.amplitude?.track(event)
    }

    fun GetTransferDetails(): TransferDetails
    {
        var tpcnt = 0
        var rtpcnt = 0
        var tp: List<TransferPoint> = listOf()
        var rtp: List<TransferPoint> = listOf()
        var tpsource: List<TransferPoint> = listOf()
        var ndrsource: List<NonDirectResult> = listOf()
        val ExRes = GlobalStuff.ExtResult
        if (ExRes != null)
        {
            if (ExRes.TransferPoints != null) {
                tp = ExRes.TransferPoints
            }
            if (ExRes.ResultTransferPoints != null) {
                rtp = ExRes.ResultTransferPoints
            }
            if (tp != null) {
                tpcnt = tp.size
            }
            if (rtp != null) {
                rtpcnt = rtp.size
            }
            if (ExRes.NonDirectRes != null) {
                ndrsource = ExRes.NonDirectRes
            }

            if (rtpcnt == 0)
            {
                tpsource = tp
            }
            else
            {
                tpsource = rtp
            }
        }
        val res = TransferDetails(tpsource, ndrsource)
        return res
    }

    fun GetTransferExtra(change: String): TransferExtra?
    {
        val ExRes = GlobalStuff.ExtResult
        if (ExRes != null)
        {
            val filt = ExRes.NonDirectRes.filter{ x -> x.Transfer == change }.first()
            return TransferExtra(filt.To_airport_transfer, filt.From_airport_transfer)
        }
        else
        {
            return null
        }
    }

    fun GetAppSessionCount(key: String): Int
    {
        var result = 0
        if (GlobalStuff.prefs.contains(key)) {
            result = GlobalStuff.prefs.getString(key, "0")!!.toInt()
        }
        if (key == "appSessionsCount") {
            GlobalStuff.appSessionsCount = result
        }
        else
        {
            GlobalStuff.appSessionsCountGlobal = result
        }
        return result
    }

    fun SaveAppSessionCount(key: String)
    {
        val editor = GlobalStuff.prefs.edit()
        var res = 0
        if (key == "appSessionsCount")
        {
            res = GlobalStuff.appSessionsCount
        }
        else
        {
            res = GlobalStuff.appSessionsCountGlobal
        }
        editor.putString(key, res.toString()).apply()
    }

    fun GetRatingEventsCount()
    {
        if (GlobalStuff.prefs.contains("ratingEventsCount")) {
            GlobalStuff.ratingEventsCount = GlobalStuff.prefs.getString("ratingEventsCount", "0")!!.toInt()
        }
    }

    fun SaveRatingEventsCount()
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("ratingEventsCount", GlobalStuff.ratingEventsCount.toString() ).apply()
    }

    fun GetFirstOpenDate()
    {
        if (GlobalStuff.prefs.contains("firstOpenDate")) {
            GlobalStuff.firstOpenDate = LocalDateTime.ofEpochSecond(GlobalStuff.prefs.getString("firstOpenDate", "0")!!.toLong(), 0, ZoneOffset.UTC)
        }
    }

    fun SaveFirstOpenDate(dt: LocalDateTime)
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("firstOpenDate", dt.toEpochSecond(ZoneOffset.UTC).toString() ).apply()
    }

    fun GetFirstOpenDateGlobal()
    {
        if (GlobalStuff.prefs.contains("firstOpenDateGlobal")) {
            GlobalStuff.firstOpenDateGlobal = LocalDateTime.ofEpochSecond(GlobalStuff.prefs.getString("firstOpenDateGlobal", "0")!!.toLong(), 0, ZoneOffset.UTC)
        }
    }

    fun SaveFirstOpenDateGlobal(dt: LocalDateTime)
    {
        val editor = GlobalStuff.prefs.edit()
        editor.putString("firstOpenDateGlobal", dt.toEpochSecond(ZoneOffset.UTC).toString() ).apply()
    }

    // Функция инкремента/инициализации счетчиков сессий и даты первого запуска вызывается:
    // - каждый раз при старте аппа
    // - при переходе в активное состояние из фонового состояния (т.е. когда апп был запущен, но не использовался, и пользователь переключается на него)
    fun SetDefaultsForRating()
    {
        GlobalStuff.appSessionsCount = GetAppSessionCount("appSessionsCount") + 1
        SaveAppSessionCount("appSessionsCount")

        GlobalStuff.appSessionsCountGlobal = GetAppSessionCount("appSessionsCountGlobal") + 1
        SaveAppSessionCount("appSessionsCountGlobal")

        GetFirstOpenDate()
        GetFirstOpenDateGlobal()

        if (GlobalStuff.appSessionsCount == 1)
        {
            GlobalStuff.firstOpenDate = LocalDateTime.now()
            SaveFirstOpenDate(LocalDateTime.now())
        }
        if (GlobalStuff.appSessionsCountGlobal == 1)
        {
            GlobalStuff.firstOpenDateGlobal = LocalDateTime.now()
            SaveFirstOpenDateGlobal(LocalDateTime.now())
        }
    }

    // Функция инкремента ratingEventsCount вызывается:
    // - при нажатии пользователем кнопки поиска (непосредственно перед самим поиском)
    fun IncRatingEventsCount()
    {
        GetRatingEventsCount()
        GlobalStuff.ratingEventsCount += 1
        SaveRatingEventsCount()
    }

    // Показываем попап запроса рейтинга
    fun AskForRating()
    {
        // фиксируем в аналитике показ формы
        val event = GlobalStuff.GetBaseEvent("request rating", true, false)
        event.eventProperties = mutableMapOf("timeFromInstallDays" to Period.between(GlobalStuff.firstOpenDate?.toLocalDate(), LocalDate.now()).days,
            "timeFromInstallDaysG" to Period.between(GlobalStuff.firstOpenDateGlobal?.toLocalDate(), LocalDate.now()),
            "sessionsAmount" to GlobalStuff.appSessionsCount,
            "sessionsAmountG" to GlobalStuff.appSessionsCountGlobal,
            "eventsAmount" to GlobalStuff.ratingEventsCount,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)

        ResetCounters()

        // показываем стандартный контрол запроса рейтинга
        val appPackageName = "com.stukalov.staffairlines.pro"
        try
        {
            GlobalStuff.mActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
        }
        catch (ex: ActivityNotFoundException)
        {
            GlobalStuff.mActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
        }
    }

    fun ResetCounters()
    {
        // сбрасываем сбрасываемые счетчики
        GlobalStuff.appSessionsCount = 0
        GlobalStuff.firstOpenDate = LocalDateTime.now()
        SaveAppSessionCount("appSessionsCount")
        SaveFirstOpenDate(LocalDateTime.now())
    }

    // Функция проверки критериев, должны ли мы показывать попап запроса рейтинга
    fun ShouldAskForRating(): Boolean
    {
        val firstLaunchDate = GlobalStuff.firstOpenDate
        var daysFrom = Period.between(firstLaunchDate?.toLocalDate(), LocalDate.now()).days

        var appSesCnt = GlobalStuff.appSessionsCount
        var ratEventsCnt = GlobalStuff.ratingEventsCount
        // попап показываем, если:
        // - количество сессий с момента последнего сброса счетчика больше или равно 30
        // - И количество целевых действий больше или равно 30
        // - И с момента последнего сброса счетчика firstLaunchDate прошло более 7 суток
        val result = appSesCnt >= 30 //10
            && ratEventsCnt >= 30   //10
            && daysFrom >= 7.0
        return true//result
    }

    // Метод вызывается на форме детализации рейса через 5 секунд после открытия, если не произошло ни одно из событий:
    // - пользователь кликнул на ссылку FlyZED
    // - пользователь кликнул на кнопку подписки на рейс
    // - пользователь кликнул на кнопку Back
    // - закрылась форма детализации рейса
    fun AskForRatingIfNeeded(): Boolean
    {
        if (ShouldAskForRating())
        {
            val sel = (0..1).random()
            if (sel == 0) {
                AskForRating()
            } else {
                Survicate.invokeEvent("waitOnFlightDetailsScreen")
                ResetCounters()
            }
        }
        return false
    }
}