package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.Airline


class AirlineResponse(status: Constants.Status,
                      code: Int,
                      message: String?,
                      val airlines: List<Airline>) : BaseResponse(status, code, message)