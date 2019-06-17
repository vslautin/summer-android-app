package airport.transfer.sale.rest.models.response

import com.google.gson.annotations.SerializedName


class PublicIdResponse(@SerializedName("publicID") val publicId: String) : BaseResponse() {
}