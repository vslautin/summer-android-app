package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class User(@PrimaryKey var id: Long = 0,
                var phone: Long = 0,
                var email: String? = null,
                @SerializedName("first_name") var firstName: String? = null,
                @SerializedName("last_name") var lastName: String? = null,
                @SerializedName("receive_emails") var receiveEmails: Int? = 0,
                @SerializedName("last_sign_in") var lastSignIn: Long = 0,
                @SerializedName("created_at") var createdAt: Long = 0,
                @SerializedName("updated_at") var updatedAt: Long = 0): RealmObject()