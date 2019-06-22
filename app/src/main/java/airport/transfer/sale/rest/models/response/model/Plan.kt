package airport.transfer.sale.rest.models.response.model

import airport.transfer.sale.rest.models.response.model.v2.Coupon
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class Plan(var id: String = "",
                var provider_id: String = "",
                var provider_price: String = "",
                var markup: Int = 0,
                var coupon: Coupon? = null,
                var cur: String = "",
                var rate: String = "",
                var price_rub: Int = 0,
                var price: String = "",
                @SerializedName("pay_price") var payPrice: Int = 0,
                var pay_price_rub: Int = 0,
                @SerializedName("view_price") var viewPrice: String = "",
                var about_language: String = "", //
                var title: String = "",
                var ref: String = "",

                var passenger: Int = 0,
                var baggage: Int = 0,
                var image: String? = ""
) : RealmObject()