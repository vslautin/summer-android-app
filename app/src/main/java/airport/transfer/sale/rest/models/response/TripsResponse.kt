package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.v2.Order


class TripsResponse(status: Constants.Status,
                    code: Int,
                    message: String?,
                    val orders: List<Order>) : BaseResponse(status, code, message)