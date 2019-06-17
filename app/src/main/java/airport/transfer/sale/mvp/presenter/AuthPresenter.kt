package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.AuthView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.request.TokenRequest
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class AuthPresenter : BasePresenter<AuthView>() {

    fun getCode(phone: String) {
        restService.getCode(phone).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onPhoneSent(phone) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun getToken(phone: String, code: String) {
        restService.getToken(TokenRequest(phone, code)).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onTokenReceived(response) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun logout(token: String){
        restService.logout(token).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onLogout() },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

}