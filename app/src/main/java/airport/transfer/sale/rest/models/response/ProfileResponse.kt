package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.User


open class ProfileResponse(status: Constants.Status,
                           code: Int,
                           message: String?,
                           val user: User) : BaseResponse(status, code, message)