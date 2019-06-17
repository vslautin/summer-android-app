package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.mvp.model.CarPriceModel
import airport.transfer.sale.mvp.view.DeliveryView

@InjectViewState
class DeliveryPresenter : BasePresenter<DeliveryView>() {

    fun getCarPrices(){
        viewState.onPricesReceived(listOf(CarPriceModel(0, "Economy", "500 rubles per hour", false),
                CarPriceModel(1, "Business", "1500 rubles per hour", false)))
    }
}