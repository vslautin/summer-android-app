package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.Constants
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.ServicePlanView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.response.model.v2.Order
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class ServicePlanPresenter : BasePresenter<ServicePlanView>() {

    fun getPlans() {
        restService.getPlans().observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onPlansReceived(response) },
                { error -> networkError(error) }
        )
    }

    fun getPlans(order: Order, coupon: String) {
        restService.getPlans(order, coupon).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onPlansReceived(response)
                    } else {
                        networkError(IllegalArgumentException(response.message ?: "no message"))
                    }
                },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}