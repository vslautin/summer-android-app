package airport.transfer.sale.ui.fragment

import com.arellomobile.mvp.MvpAppCompatFragment
import airport.transfer.sale.makeToast
import airport.transfer.sale.mvp.view.BaseView


abstract class BaseFragment : MvpAppCompatFragment(), BaseView {
    override fun onError(code: Int, message: String) {
        context.makeToast(message)
    }
}