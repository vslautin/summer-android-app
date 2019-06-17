package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.User


class TokenResponse(status: Constants.Status,
                    code: Int,
                    message: String?,
                    user: User,
                    val token: String) : ProfileResponse(status, code, message, user)