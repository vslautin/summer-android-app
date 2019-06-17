package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants


class PriceResponse (status: Constants.Status,
                     code: Int,
                     message: String?,
                     val cost: Int?) : BaseResponse(status, code, message)