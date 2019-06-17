package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName


data class AddressComponent(@SerializedName("long_name") val longName: String,
                            @SerializedName("short_name") val shortName: String,
                            val types: List<AddressComponentType>)