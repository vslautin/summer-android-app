package airport.transfer.sale.ui.fragment.order.param

import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import airport.transfer.sale.R
import airport.transfer.sale.adapter.CarClassAdapter
import airport.transfer.sale.adapter.ViewHolderWrapper
import airport.transfer.sale.mvp.model.CarPriceModel
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_recycler_view)
open class PlansFragment : BottomSheetDialogFragment(){

    private var listener: ChoosePlanListener? = null

    private var plansAdapter: CarClassAdapter? = null

    @AfterViews
    fun ready(){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        if (plansAdapter == null) plansAdapter = CarClassAdapter()
        plansAdapter?.let {
            it.setClickListener(object : ViewHolderWrapper.ClickListener {
                override fun onItemClicked(position: Int, wrapper: ViewHolderWrapper<*>) {
                    listener?.onPlanChosen(it.getItem(position))
                }
            })
        }
        recyclerView.adapter = plansAdapter
    }

    interface ChoosePlanListener {
        fun onPlanChosen(model: CarPriceModel)
    }

    fun setChoosePlanListener(listener: ChoosePlanListener){
        this.listener = listener
    }

    fun setPlans(cities: List<CarPriceModel>){
        if (plansAdapter == null) plansAdapter = CarClassAdapter()
        plansAdapter?.replaceAll(cities)
    }
}