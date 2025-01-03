package com.stukalov.staffairlines.pro

import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.textclassifier.ConversationActions
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stukalov.staffairlines.pro.ui.result.ResultFragment
import okhttp3.ConnectionSpec
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Arrays


class StaffMethods {

    val BaseUrl: String = "https://api.staffairlines.com:8033/api"
    var StaffApp = StaffAirlines()
    val client = OkHttpClient().newBuilder().connectionSpecs(
        Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS)).addInterceptor { chain ->
        val originalRequest = chain.request()

        val builder = originalRequest.newBuilder()
            .header("Authorization", Credentials.basic("sae2", "ISTbetweenVAR1999"))
        val newRequest = builder.build()
        chain.proceed(newRequest)
    }.build()

    fun ExtendedSearch(origin: String, destination: String, date: LocalDate, list: String, GetTransfer: Boolean, ntype: GetNonDirectType, pax: Int, currency: String, lang: String, country: String, token: String = "void token", sa: Boolean = true, ver: String = "1.0", ac: String = "--"): String
    {
        var res: String = ""
        try
        {
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val Uri = BaseUrl + "/amadeus/ExtendedSearch?origin=" + origin + "&destination=" + destination + "&date=" + date.format(formatter) + "&list=" + list + "&GetTransfer=" + GetTransfer.toString() + "&GetNonDirect=" + ntype.toString() + "&pax=" + pax + "&token=" + token + "&sa=" + sa.toString() + "&ver=" + ver + "&ac=" + ac + "&currency=" + currency + "&lang=" + lang + "&country=" + country;
            val request = Request.Builder().url(Uri).build()

            val Json = RequestJson(client, request)

            val ExtData: ExtendedResult
            val gson = Gson()

            try {
                ExtData = gson.fromJson(Json, ExtendedResult::class.java)
                GlobalStuff.ExtResult = ExtData

                if (GlobalStuff.ExtResult != null && GlobalStuff.ExtResult?.DirectRes?.count()!! > 0)
                {
                    //direct_lv.setAdapter(resultadapter)
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
            StaffApp.Locations = LocData.toList()

            GlobalStuff.Locations = LocData.toList()
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
        return Json
    }

    fun RequestJson(client: OkHttpClient, request: Request): String {
        var Json: String = ""

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Запрос к серверу не был успешен:" +
                            " ${response.code} ${response.message}")
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
}