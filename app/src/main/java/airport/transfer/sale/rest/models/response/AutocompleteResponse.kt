package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants
import airport.transfer.sale.rest.models.response.model.v2.Address


class AutocompleteResponse(status: Constants.Status,
                           code: Int,
                           message: String?,
                           val results: List<Address>) : BaseResponse(status, code, message)