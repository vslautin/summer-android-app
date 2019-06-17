package airport.transfer.sale.rest.models.response

import com.google.gson.annotations.SerializedName
import airport.transfer.sale.mvp.model.CreditCard


class CreditCardsResponse(@SerializedName("payments") val cards: List<CreditCard>) : BaseResponse() {
}