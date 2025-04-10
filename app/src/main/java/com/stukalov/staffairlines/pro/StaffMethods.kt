package com.stukalov.staffairlines.pro

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
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
        val res = GlobalStuff.Permitted.joinToString ("-") { it.Code }
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
}