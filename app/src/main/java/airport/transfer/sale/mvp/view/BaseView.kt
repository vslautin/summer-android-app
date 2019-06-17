package airport.transfer.sale.mvp.view

import com.arellomobile.mvp.MvpView


interface BaseView : MvpView {
    fun onError(code: Int, message: String)
}