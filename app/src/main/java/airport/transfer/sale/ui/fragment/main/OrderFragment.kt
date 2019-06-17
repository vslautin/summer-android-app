package airport.transfer.sale.ui.fragment.main

import android.support.v4.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import airport.transfer.sale.R
import airport.transfer.sale.mvp.presenter.OrderPresenter
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.order.OrderCreationFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_container)
open class OrderFragment : BaseFragment(), OrderView{

    @InjectPresenter
    lateinit var presenter: OrderPresenter

    @AfterViews
    fun ready(){
        presenter.getCurrentState()
    }

    override fun onCurrentStateReceived(edit: Boolean, back: Boolean) {
        unsafeShowFragment(OrderCreationFragment.newInstance(back))//todo refactor
    }

    override fun onOrderCreated(order: Order, transaction: Transaction?) {
    }

    override fun onOrderCancelled(positionToRemove: Int) {
    }

    fun showFragment(fragment: Fragment){
        childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                .commit()
    }

    private fun unsafeShowFragment(fragment: Fragment){
        childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                .commitAllowingStateLoss()
    }

    override fun onTokenError() {

    }
}