package airport.transfer.sale.mvp.view

import com.arellomobile.mvp.MvpView
import airport.transfer.sale.mvp.model.CarPriceModel


interface DeliveryView : MvpView {
    fun onPricesReceived(prices: List<CarPriceModel>)
}