package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction

interface OrderView : BaseView {

    fun onCurrentStateReceived(edit: Boolean, back: Boolean)

    fun onOrderCreated(order: Order, transaction: Transaction?)

    fun onOrderCancelled(positionToRemove: Int)

    fun onTokenError()
}