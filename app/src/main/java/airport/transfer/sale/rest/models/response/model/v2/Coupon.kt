package airport.transfer.sale.rest.models.response.model.v2

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class Coupon(var discount: Int = 0,
                  @SerializedName("price_rub") var priceRub: Int = 0,
                  @SerializedName("pay_price") var payPrice: Int = 0,
                  var price: String? = "",
                  @SerializedName("view_price") var viewPrice: String? = ""): RealmObject()