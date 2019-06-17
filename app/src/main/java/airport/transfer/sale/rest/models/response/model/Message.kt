package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName


data class Message(val body: String,
                   val id: Int,
                   @SerializedName("created_at") val createdAt: Long,
                   val author: User)