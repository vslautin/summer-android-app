package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.Message

class MessageResponse (status: Constants.Status,
                       code: Int,
                       item: String?,
                       val body: Message) : BaseResponse(status, code, item)
