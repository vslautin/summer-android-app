package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.v2.Order


interface TripsView : BaseView{

    fun onCurrentTripsReceived(trips: List<Order>)

    fun onCompletedTripsReceived(trips: List<Order>)

    fun onFavoriteTripsReceived(trips: List<Order>)

    fun onFavoriteAdded(order: Order)

    fun onTokenError()
}