package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.Airline


interface AirlinesView : BaseView {

    fun onAirlinesReceived(airlines: List<Airline>)
}