package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.Airport


class SearchAirportResponse(status: Constants.Status,
                               code: Int,
                               message: String?,
                               val result: List<Airport>) : BaseResponse(status, code, message)