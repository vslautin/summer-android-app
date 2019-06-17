package airport.transfer.sale.rest.models.response

import com.google.gson.annotations.SerializedName
import airport.transfer.sale.mvp.model.CreditCard


class CreditCardResponse(@SerializedName("payment") val card: CreditCard) : BaseResponse()