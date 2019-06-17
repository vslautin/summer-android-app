package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.response.model.v2.Order
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class OrderPresenter : BasePresenter<OrderView>() {

    fun getCurrentState() {
        viewState.onCurrentStateReceived(false, false)
    }

    fun createOrder(token: String, order: Order, crypto: String? = null) {
        restService.createOrder(token, order, crypto).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    viewState.onOrderCreated(response.order ?: Order(), response.payment)
                },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun editOrder(token: String, order: Order) {
        restService.editOrder(token, order.id!!, order).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onOrderCreated(response.order ?: Order(), null) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun addFeedback(token: String, order: Order) {
        restService.addFeedback(token, order.id!!, order.review?.comment, order.review?.rate!!).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onOrderCreated(response.order ?: Order(), null) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun cancelOrder(token: String, id: Long, positionToRemove: Int) {
        restService.cancelOrder(token, id).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onOrderCancelled(positionToRemove) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}