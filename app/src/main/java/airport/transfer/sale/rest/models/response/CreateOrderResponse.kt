package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction


class CreateOrderResponse (status: Constants.Status,
                           code: Int,
                           message: String?,
                           val order: Order?,
                           val payment: Transaction?) : BaseResponse(status, code, message)