package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.PaymentView
import airport.transfer.sale.networkError
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>() {

    fun addCard(token: String, holder: String, crypto: String, number: String){
        restService.addCard(token, holder, crypto, number).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onCreditCardAdded(response.card) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun getPublicId(token: String){
        restService.getPublicId(token).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onPublicIdReceived(response.publicId) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun getCards(token: String){
        restService.getCards(token).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onCreditCardsReceived(response.cards) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun deleteCard(token: String, cardId: Long, adapterPosition: Int){
        restService.deleteCard(token, cardId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onCreditCardRemoved(cardId, adapterPosition, response.id) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun defaultCard(token: String, cardId: Long){
        restService.defaultCard(token, cardId).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onDefaultChanged(cardId) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}