package airport.transfer.sale.rest.models.response.model.v2

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class Address(var title: String = "",
                   var subtitle: String = "",
                   var lat: Float? = 200f,
                   var lng: Float? = 200f,
                   @SerializedName("is_airport") var isAirport: Boolean? = false,
                   @SerializedName("is_transfer") var isTransfer: Boolean = true,
                   var isForcedAirport: Boolean = false): RealmObject(){


}