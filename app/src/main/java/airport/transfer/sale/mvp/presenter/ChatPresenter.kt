package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.Constants
import airport.transfer.sale.mvp.view.ChatView
import airport.transfer.sale.networkError
import retrofit2.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class ChatPresenter : BasePresenter<ChatView>() {

    fun sendMessage(message: String, token: String){
        restService.sendMessage(message, token).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> if (response.status == Constants.Status.SUCCESS) viewState.onMessageSent(response.body)},
                { error -> networkError(error) }
        )
    }

    fun getMessages(token: String){
        restService.getMessages(token, 20, 0).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onMessagesReceived(response.messages) },
                { error -> networkError(error) }
        )
    }

    fun sendFeedback(message: String, token: String){ // new API
        restService.sendFeedback(message, token).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> if (response.status == Constants.Status.SUCCESS) viewState.onFeedbackSent()},
                { error ->
                    if (error is HttpException && error.code() == 403) viewState.onMissedEmail()
                    else networkError(error) }
        )
    }
}