package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.PriceView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.response.model.v2.Order
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class PricePresenter : BasePresenter<PriceView>() {

    fun getPrice(order: Order) {
        if (!order.delivery?.title.isNullOrEmpty() && !order.destination?.title.isNullOrEmpty() &&
                order.delivery?.lat != 200f && order.destination?.lat != 200f &&
                order.plan != null && !order.deliveryTime.isNullOrEmpty()) {
            restService.getPrice(order).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { response -> response.cost?.let { viewState.onPriceReceived(it) } },
                    { error ->
                        if (error is HttpException) {
                            val message = error.getErrorString()
                            if (message != null) viewState.onError(error.code(), message)
                        } else networkError(error)
                    }
            )
        }
    }
}