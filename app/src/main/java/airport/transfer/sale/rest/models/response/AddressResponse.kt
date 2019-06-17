package airport.transfer.sale.rest.models.response

import airport.transfer.sale.Constants


class AddressResponse(status: Constants.Status,
                      code: Int,
                      message: String?,
                      val address: String,
                      val title: String,
                      val subtitle: String,
                      val lat: Float,
                      val lng: Float) : BaseResponse(status, code, message)