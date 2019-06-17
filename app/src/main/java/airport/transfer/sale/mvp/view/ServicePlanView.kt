package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.PlansResponse


interface ServicePlanView : BaseView {
    fun onPlansReceived(plans: PlansResponse)
}
