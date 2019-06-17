package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants.Status

open class BaseResponse(val status: Status = Status.FAIL,
                        val code: Int = 200,
                        val message: String? = null)