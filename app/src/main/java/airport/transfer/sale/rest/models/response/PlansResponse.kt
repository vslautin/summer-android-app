package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.Plan


open class PlansResponse (status: Constants.Status,
                          code: Int,
                          message: String?,
                          val plans: List<Plan>) : BaseResponse(status, code, message)