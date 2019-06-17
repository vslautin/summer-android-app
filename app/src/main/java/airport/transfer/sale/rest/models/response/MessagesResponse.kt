package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.Message


class MessagesResponse (status: Constants.Status,
                        code: Int,
                        message: String?,
                        val messages: List<Message>) : BaseResponse(status, code, message)