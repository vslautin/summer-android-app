package airport.transfer.sale.rest.models.response.model

import airport.transfer.sale.rest.models.response.model.v2.Coupon
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class Plan(var id: Int = 0,
                var title: String = "",
                var about: String = "",
                var passenger: Int = 0,
                var baggage: Int = 0,
                var price: String = "",
                @SerializedName("view_price") var viewPrice: String = "",
                @SerializedName("pay_price") var payPrice: Int = 0,
                var image: String? = "",
                var coupon: Coupon? = null) : RealmObject()