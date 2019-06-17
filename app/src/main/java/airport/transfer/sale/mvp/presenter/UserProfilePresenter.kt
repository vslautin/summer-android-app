package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import airport.transfer.sale.getErrorString
import airport.transfer.sale.mvp.view.UserProfileView
import airport.transfer.sale.networkError
import airport.transfer.sale.rest.models.request.UpdateProfileRequest
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class UserProfilePresenter : BasePresenter<UserProfileView>() {

    fun updateProfile(authToken: String, name: String, lastName: String?, email: String, receiveEmails: Boolean?) {
        restService.updateUser(authToken, UpdateProfileRequest(email, name, lastName,
                if (receiveEmails == null) null else if (receiveEmails) 1 else 0))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onProfileUpdated(response.user) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }

    fun getUser(token: String?){
        restService.getUser(token!!).observeOn(AndroidSchedulers.mainThread()).subscribe(
                { response -> viewState.onUserReceived(response.user) },
                { error ->
                    if (error is HttpException) {
                        val message = error.getErrorString()
                        if (message != null) viewState.onError(error.code(), message)
                    } else networkError(error)
                }
        )
    }
}