package com.stukalov.staffairlines.pro

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

class StaffModels
{

}

enum class PointType {
    Origin, Destination
}

enum class GetNonDirectType(val value: Int)
{
    Off(0),
    On(1),
    Auto(2)
}

enum class RType(val value: Int)
{
    Red(1),
    Yellow(2),
    Green(3)
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

data class Flight(
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

    var Rating: RType,

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
    var City: String,
    var CityName: String,
    var Country: String,
    var CountryName: String,
    var Distance: Int?
)

data class NonDirectResult(
    var Transfer: String,
    var To_airport_transfer: List<Flight>,
    var From_airport_transfer: List<Flight>,
    var ToTransferInfo: AviasalesInfo,
    var FromTransferInfo: AviasalesInfo,
    var RedCount: Int,
    var YellowCount: Int,
    var GreenCount: Int,
    var FirstFlightTransfer: LocalDate,
    var LastFlightTransfer: LocalDate,
    var AirportsOrigin: List<TransferPoint>,
    var AirportsDestination: List<TransferPoint>,
    var Log: String,
    var Alert: String
)

data class ExtendedResult(
    var DirectRes: List<Flight>,
    var DirectInfo: AviasalesInfo,
    var TransferPoints: List<TransferPoint>,
    var NonDirectRes: List<NonDirectResult>,
    var AirportsOrigin: List<TransferPoint>,
    var AirportsDestination: List<TransferPoint>,
    var ResultTransferPoints: List<TransferPoint>,
    var Log: String,
    var Alert: String
)


