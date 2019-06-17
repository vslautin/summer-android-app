package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class PlanPrice (var price: Int = 0,
                      @SerializedName("price_rup") var rubPrice: Int = 0,
                      var markup: Int = 0): RealmObject()