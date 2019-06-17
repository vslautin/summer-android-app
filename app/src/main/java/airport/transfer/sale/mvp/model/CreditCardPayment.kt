package airport.transfer.sale.mvp.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class CreditCardPayment(@PrimaryKey var id: Long = 0,
                        @SerializedName("card_holder") var cardHolder: String = "",
                        @SerializedName("first_number") var firstDigits: String = "",
                        @SerializedName("last_number") var lastDigits: String = "",
                        var type: String = "",
                        @SerializedName("default") var isDefault: Boolean = false,
                        @SerializedName("created_at") var createdAt: Long = 0,
                        @SerializedName("updated_at") var updatedAt: Long = 0) : RealmObject() {

    fun getTitle() = String.format(Locale.ENGLISH, "%s %s .... .... %s", type, firstDigits, lastDigits)
}