package airport.transfer.sale.rest.models.response.model.v2

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import airport.transfer.sale.mvp.model.CreditCardPayment
import airport.transfer.sale.rest.Api
import airport.transfer.sale.rest.models.response.model.Plan
import java.util.*


open class Order(@PrimaryKey var id: Long? = 0,
                 var delivery: Address? = Address(isAirport = true),
                 var destination: Address? = Address(isAirport = false),
                 var plan: Plan? = null,
                 @SerializedName("flight_number") var flightNumber: String? = "",
                 @SerializedName("delivery_time") var deliveryTime: String? = "",
                 var cost: Int? = 0,
                 var price: String = "",
                 @SerializedName("view_price") var viewPrice: String = "",
                 var status: Int? = 0,
                 @SerializedName("is_favorite") var isFavorite: Boolean? = false,
                 @SerializedName("updated_at") var updatedAt: Long? = 0,
                 var review: Review? = Review(),
                 @SerializedName("is_review_proposed") var isReviewProposed: Boolean? = false,
                 @SerializedName("baby_chair") var babyChair: Int? = 0,
                 @SerializedName("payment") var creditCard: CreditCardPayment? = null,
                 var code: String = "",
                 var couponName: String? = null) : RealmObject() {

    private fun getRequest(edit: Boolean): HashMap<String, String> {
        val map = HashMap<String, String>()
        with(map) {
            put("plan_id", plan?.id!!.toString())
            put("delivery_time", deliveryTime!!)
            put("baby_chair", babyChair!!.toString())
            putAll(delivery!!.getRequestMap("delivery"))
            putAll(destination!!.getRequestMap("destination"))
            if (flightNumber != null) put("flight_number", flightNumber!!)
            if (edit) {
                if (isFavorite != null) put("is_favorite", if (isFavorite == true) "1" else "0")
                if (isReviewProposed != null) put("is_review_proposed", if (isReviewProposed == true) "1" else "0")
            }
            couponName?.let{ put("coupon", it) }
        }
        if (!edit) map.put("code", Locale.getDefault().language)
        return map
    }

    fun getPlansRequest(): HashMap<String, String> {
        val map = HashMap<String, String>()
        with(map) {
            putAll(delivery!!.getRequestMap("delivery"))
            putAll(destination!!.getRequestMap("destination"))
        }
        return map
    }

    fun getRequestString(edit: Boolean, token: String?): String {
        val sb = StringBuilder()
        getRequest(edit).entries.forEach { sb.append(it.key + "=" + it.value + "&") }
        val result = sb.toString().substringBeforeLast("&")
                .plus("&${Api.LANGUAGE}=${Locale.getDefault().language}")
        return if (token == null) result else result.plus("&${Api.AUTHORIZATION}=$token")
    }

    fun getPlansRequestString(coupon: String): String {
        val sb = StringBuilder()
        getPlansRequest().entries.forEach { sb.append(it.key + "=" + it.value + "&") }
        return sb.toString().substringBeforeLast("&")
                .plus("&${Api.LANGUAGE}=${Locale.getDefault().language}")
                .plus("&coupon=$coupon")
    }

    private fun Address.getRequestMap(name: String): Map<String, String> {
        val map = HashMap<String, String>()
        with(map) {
            put("$name[title]", title)
            put("$name[subtitle]", subtitle)
            put("$name[lat]", lat.toString())
            put("$name[lng]", lng.toString())
            put("$name[is_airport]", if (isAirport == false) "0" else "1")
        }
        return map
    }

    override fun toString(): String =
            "Address($id, deliveryTitle=${delivery?.title}, destinationTitle=${destination?.title}"
}