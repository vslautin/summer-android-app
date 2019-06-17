package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName


enum class AddressComponentType {
    @SerializedName("administrative_area_level_1") ADMINISTRATIVE_AREA_LEVEL_1,
    @SerializedName("administrative_area_level_2") ADMINISTRATIVE_AREA_LEVEL_2,
    @SerializedName("airport") AIRPORT,
    @SerializedName("country") COUNTRY,
    @SerializedName("locality") LOCALITY,
    @SerializedName("route") ROUTE,
    @SerializedName("street_number") STREET_NUMBER
}