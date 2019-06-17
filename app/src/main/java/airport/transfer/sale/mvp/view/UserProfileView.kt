package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.User


interface UserProfileView : BaseView{

    fun onProfileUpdated(user: User)

    fun onUserReceived(user: User)

    fun onTokenError()
}