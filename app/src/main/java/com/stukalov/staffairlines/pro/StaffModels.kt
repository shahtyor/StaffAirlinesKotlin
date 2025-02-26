package com.stukalov.staffairlines.pro

import android.text.Spanned
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.ArrayList

class StaffModels
{

}

val secondFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
val SM: StaffMethods = StaffMethods()

enum class PointType {
    Origin, Destination
}

enum class GetNonDirectType(val value: Int)
{
    off(0),
    on(1),
    auto(2)
}

enum class RType(val value: Int)
{
    Bad(1),
    Medium(2),
    Good(3);

    companion object {
        fun fromInt(value: Int) = RType.values().first { it.value == value }
    }
}

enum class ResultType
{
    Direct, First, Second, Final
}

data class Location(
    @SerializedName("id")
    var Id: Int,
    @SerializedName("name")
    var Name: String,
    @SerializedName("name_en")
    var Name_en: String,
    @SerializedName("code")
    var Code: String,
    @SerializedName("name_country")
    var Name_country: String)
{
    val NameWithCountry: String
        get() = Name_en + ", " + Name_country

    val ImagePath: String
        get() = if (Code.isNullOrEmpty()) "hotel.png"
                else "engine.png"

    val CodeEnh: String
        get() = if (Code.isNullOrEmpty()) "All airports near " + Name_en
                else Code
}

data class SelectedPoint(
    var Id: Int,
    var Name: String,
    var Code: String,
    var PType: PointType,
    var CountryName: String)

class Flight(
    /// <summary>
    /// Код аэропорта вылета
    /// </summary>
    var Origin: String,
    /// <summary>
    /// Код аэропорта прилета
    /// </summary>
    var Destination: String,

    var DepartureName: String,
    /// <summary>
    /// Терминал вылета
    /// </summary>
    var DepartureTerminal: String,

    var DepartureCity: String,

    var DepartureCityName: String,

    var ArrivalName: String,
    /// <summary>
    /// Терминал прилета
    /// </summary>
    var ArrivalTerminal: String,

    var ArrivalCity: String,

    var ArrivalCityName: String,

    var OriginDistance: Int?,

    var DestinationDistance: Int?,
    /// <summary>
    /// Номер рейса
    /// </summary>
    var FlightNumber: String,
    /// <summary>
    /// Оперирующая авиакомпания
    /// </summary>
    var OperatingCarrier: String,
    /// <summary>
    /// Маркетинговая авиакомпания
    /// </summary>
    var MarketingCarrier: String,

    var OperatingName: String,

    var MarketingName: String,
    /// <summary>
    /// Дата и время вылета
    /// </summary>
    var DepartureDateTime: String,
    /// <summary>
    /// Дата и время прилета
    /// </summary>
    var ArrivalDateTime: String,
    /// <summary>
    /// Тип самолета
    /// </summary>
    var Equipment: String,

    var EquipmentName: String,
    /// <summary>
    /// Время в пути в минутах
    /// </summary>
    var Duration: Int,
    /// <summary>
    /// Список классов бронирования с количеством мест (L4, K5, M3, …)
    /// </summary>
    var NumSeatsForBookingClass: Array<String>,

    /// <summary>
    /// Количество  SA пассажиров
    /// </summary>
    var CntSAPassenger: Int?,

    var Sum_subclass_variant: Int,
    var EconomyPlaces: Int,
    var BusinessPlaces: Int,
    var AllPlaces: String,
    @SerializedName("Rating")
    var Rating: Int,

    /// <summary>
    /// Прогноз загрузки
    /// </summary>
    var Forecast: Float,

    /// <summary>
    /// Количество данных в статистике, на основании которых сделан прогноз
    /// </summary>
    var C: Long,

    /// <summary>
    /// Точность прогноза
    /// </summary>
    var Accuracy: String,

    var AgentInfo: PlaceInfo,

    var Reporters: Boolean
)
{
    val RatingType: RType
        get() = RType.fromInt(Rating)

    val DepDateTime: LocalDateTime
        get() = LocalDateTime.parse(DepartureDateTime, secondFormatter)

    val ArrDateTime: LocalDateTime
        get() = LocalDateTime.parse(ArrivalDateTime, secondFormatter)
    val InFav: Boolean
        get() = SM.ExistInFavourites(this)
}

class FlightWithPax(
    var Fl: Flight,
    var pax: Int,
    var Dt: Long
)

class FlightInfo(
    var Flight: Flight,
    var Alert: String
)

class HistoryElement
(
    var Origin: String,
    var Destination: String,
    var OriginId: String,
    var DestinationId: String,
    var OriginName: String,
    var DestinationName: String,
    var SearchDate: Long,
    var Pax: Int
)

class Airline0
(
    @SerializedName("code")
    var Code: String,
    @SerializedName("airline")
    var Airline: String
)

class PermittedAC
(
    @SerializedName("code")
    var Code: String,
    @SerializedName("permit")
    var Permit: String
)

class CurDateTime
(
    var Time: LocalDateTime,
    var TimeServer: LocalDateTime
)

class CurDateTimeString
(
    var Time: String,
    var TimeServer: String
)

class TransferDetails
(
    var tp: List<TransferPoint>,
    var ndr: List<NonDirectResult>,
)

class CarouselData
(
    var title: String,
    var image: String,
    var desc: Spanned
)

data class PlaceInfo(
    var EconomyPlaces: Int?,
    var BusinessPlaces: Int?,
    var CntSAPassenger: Int?,
    var ts: LocalDate,
    var TimePassed: Int,
    var Nickname: String,
    var Id_user: String
)

data class AviasalesInfo(
    var Amount: Float,
    var Currency: String,
    var Link: String,
    var FoundAt: String,
    var Source: String
)

data class TransferPoint(
    /// <summary>
    /// Код аэропорта пересадки
    /// </summary>
    var Origin: String,
    var Name: String,
    var City: String?,
    var CityName: String?,
    var Country: String,
    var CountryName: String,
    var Distance: Int?
)

data class NonDirectResult(
    var Transfer: String,
    @SerializedName("to_airport_transfer")
    var To_airport_transfer: ArrayList<Flight>,
    @SerializedName("from_airport_transfer")
    var From_airport_transfer: ArrayList<Flight>,
    var ToTransferInfo: AviasalesInfo,
    var FromTransferInfo: AviasalesInfo,
    var RedCount: Int,
    var YellowCount: Int,
    var GreenCount: Int,
    var FirstFlightTransfer: String,
    var LastFlightTransfer: String,
    var AirportsOrigin: ArrayList<TransferPoint>,
    var AirportsDestination: ArrayList<TransferPoint>,
    var Log: String,
    var Alert: String
)

data class ExtendedResult(
    var DirectRes: ArrayList<Flight>,
    var DirectInfo: AviasalesInfo,
    var TransferPoints: ArrayList<TransferPoint>,
    var NonDirectRes: ArrayList<NonDirectResult>,
    var AirportsOrigin: ArrayList<TransferPoint>,
    var AirportsDestination: ArrayList<TransferPoint>,
    var ResultTransferPoints: ArrayList<TransferPoint>,
    var Log: String,
    var Alert: String?
)


