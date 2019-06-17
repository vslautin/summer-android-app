package airport.transfer.sale.mvp.presenter

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import airport.transfer.sale.rest.RestService

open class BasePresenter<T : MvpView> : MvpPresenter<T>() {
    val restService = RestService()
}