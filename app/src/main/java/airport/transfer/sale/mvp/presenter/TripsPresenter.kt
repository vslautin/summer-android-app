package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.*
import airport.transfer.sale.mvp.view.TripsView
import airport.transfer.sale.rest.models.response.model.Plan
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.rest.models.response.model.v2.Order
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class TripsPresenter : BasePresenter<TripsView>(){

    fun getCurrentOrders(authToken: String){
        restService.getCurrentOrders(authToken).observeOn(AndroidSchedulers.mainThread()).subscribe (
                {response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onCurrentTripsReceived(response.orders)
                    } else networkError(Exception(response.message!!))},
                {error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun getCompletedOrders(authToken: String, limit: Int, offset: Int){
        restService.getCompletedOrders(authToken, limit, offset).observeOn(AndroidSchedulers.mainThread()).subscribe (
                {response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onCompletedTripsReceived(/*getTestOrders(limit, false, offset)*/response.orders)
                    } else networkError(Exception(response.message!!))},
                {error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    private fun getTestOrders(size: Int, isFavorite: Boolean, offset: Int): List<Order>{
        val orders = ArrayList<Order>()
        if (offset > 50) return orders
        (0 until size).forEach{
            orders.add(Order(it.toLong() + offset,
                    Address("address$it", "subtitle$it"),
                    Address("2address$it", "2subtitle$it"),
                    Plan(it, "plan$it", "planInfo$it"),
                    "flightNumber$it",
                    (System.currentTimeMillis() + 3600 * 1000 * it).toServerDate(),
                    100500,
                    "1",
                    "1",
                    6,
                    isFavorite,
                    System.currentTimeMillis() + it,
                    isReviewProposed = true))
        }
        return orders
    }

    fun getFavoriteOrders(authToken: String, limit: Int, offset: Int){
        restService.getFavoriteOrders(authToken, limit, offset).observeOn(AndroidSchedulers.mainThread()).subscribe (
                {response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onFavoriteTripsReceived(/*getTestOrders(limit, true, offset)*/response.orders)
                    } else networkError(Exception(response.message!!))},
                {error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun addFavorite(authToken: String, order: Order){
        order.isFavorite = !order.isFavorite!!
        restService.editOrder(authToken, order.id!!, order).observeOn(AndroidSchedulers.mainThread()).subscribe (
                {response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onFavoriteAdded(response.order!!)
                    } else networkError(Exception(response.message!!))},
                {error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}