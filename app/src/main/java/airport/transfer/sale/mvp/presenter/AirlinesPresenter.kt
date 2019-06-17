package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.Constants
import airport.transfer.sale.mvp.view.AirlinesView
import airport.transfer.sale.networkError
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class AirlinesPresenter : BasePresenter<AirlinesView>(){

    fun getAirlines(query: String?){
        if (query == null) restService.getAirlines().observeOn(AndroidSchedulers.mainThread()).subscribe (
                    {response ->
                        if (response.status == Constants.Status.SUCCESS) {
                            viewState.onAirlinesReceived(response.airlines)
                        } else networkError(Exception(response.message!!))},
                    {error -> networkError(error)}
            )
        else restService.getAirlines(query).observeOn(AndroidSchedulers.mainThread()).subscribe (
                {response ->
                    if (response.status == Constants.Status.SUCCESS) {
                        viewState.onAirlinesReceived(response.airlines)
                    } else networkError(Exception(response.message!!))},
                {error -> networkError(error)}
        )
    }
}