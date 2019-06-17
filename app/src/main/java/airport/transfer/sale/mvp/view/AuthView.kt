package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.TokenResponse


interface AuthView : BaseView{
    fun onPhoneSent(phone: String){}
    fun onTokenReceived(response: TokenResponse){}
    fun onCodeDenied(message: String?){}
    fun onLogout(){}
}