package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.v2.Address


interface SplashView : BaseView{
    fun onForcedAirportReceived(airport: Address?){}
}