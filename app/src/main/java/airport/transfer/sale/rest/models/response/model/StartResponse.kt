package airport.transfer.sale.rest.models.response.model

import com.google.gson.annotations.SerializedName
import airport.transfer.sale.rest.models.response.BaseResponse
import airport.transfer.sale.rest.models.response.model.v2.Address


class StartResponse(@SerializedName("force_airport") val hasForcedAirport: Boolean,
                    val result: Address?) : BaseResponse()