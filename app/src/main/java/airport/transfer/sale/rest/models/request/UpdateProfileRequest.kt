package airport.transfer.sale.rest.models.request

import com.google.gson.annotations.SerializedName


data class UpdateProfileRequest(val email: String,
                                @SerializedName("first_name") val firstName: String,
                                @SerializedName("last_name") val lastName: String?,
                                @SerializedName("receive_emails") val receiveEmails: Int?)