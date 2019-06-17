package airport.transfer.sale.ui.fragment.order

import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import airport.transfer.sale.R
import airport.transfer.sale.clearBackground
import airport.transfer.sale.adapter.CarPriceAdapter
import airport.transfer.sale.mvp.model.CarPriceModel
import airport.transfer.sale.mvp.presenter.DeliveryPresenter
import airport.transfer.sale.mvp.view.DeliveryView
import airport.transfer.sale.showInfoPage
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.main.OrderFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

open class DeliveryFragment : BaseFragment()