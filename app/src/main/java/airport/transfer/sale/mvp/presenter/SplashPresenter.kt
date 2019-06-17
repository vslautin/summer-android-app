package airport.transfer.sale.mvp.presenter

import android.os.Handler
import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.SplashView
import airport.transfer.sale.networkError
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {

    fun loadData(){
        Handler().postDelayed({ checkForcedAirport() }, 1000)
    }

    private fun checkForcedAirport(){
        restService.checkForcedAirport().observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onForcedAirportReceived(response.result) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun addPushToken(token: String, pushToken: String){
        restService.addPushToken(token, pushToken).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response ->  },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}