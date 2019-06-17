package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName
import airport.transfer.sale.mvp.model.Location


data class Airport(val id: String?,
                   @SerializedName("is_airport") val isAirport: Boolean?,
                   val location: Location?,
                   @SerializedName("formatted_address") val formattedAddress: String,
                   @SerializedName("address_components") val addressComponents: List<AddressComponent>?,
                   var name: String?,
                   @SerializedName("place_id") val placeId: String?,
                   val types: List<AddressComponentType>?,
                   var title1: String?,
                   var subtitle1: String?,
                   var showPlane: Boolean) {

    fun getTitle(): String? {
        if (title1 != null) return title1
        if (isAirport()) return addressComponents?.find { component ->
            component.types.contains(AddressComponentType.AIRPORT)
        }?.longName ?: name
        else if (name != null) return name
        val street = addressComponents?.find {
            it.types.contains(AddressComponentType.ROUTE)
        }?.longName
        val house = addressComponents?.find {
            it.types.contains(AddressComponentType.STREET_NUMBER)
        }?.longName
        if (house != null) return street + ", " + house
        else if (street != null) return street
        return formattedAddress
    }

    fun getSubtitle(): String? {
        if (subtitle1 != null) return subtitle1
        val title = getTitle()
        if (title == formattedAddress) return null
        if (title == name) return formattedAddress
        val locality = addressComponents?.find { it.types.contains(AddressComponentType.LOCALITY) }?.longName
        val country = addressComponents?.find { it.types.contains(AddressComponentType.COUNTRY) }?.longName
        val area = addressComponents?.find { it.types.contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1) }?.longName
        var subtitle: String? = null
        if (locality != null) subtitle = locality
        else if (area != null) subtitle = area
        if (subtitle == null) return country
        country?.let { subtitle += ", " + country }
        return subtitle
    }

    fun isAirport(): Boolean{
        if (isAirport != null) return isAirport
        if (addressComponents == null && types == null) return false
        if (addressComponents == null) return types!!.contains(AddressComponentType.AIRPORT)
        return addressComponents.find { it.types.contains(AddressComponentType.AIRPORT) } != null
    }
}