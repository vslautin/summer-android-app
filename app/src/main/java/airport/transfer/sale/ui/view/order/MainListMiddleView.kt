package airport.transfer.sale.ui.view.order

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.view_main_list_item_middle.view.*
import airport.transfer.sale.R
import airport.transfer.sale.mvp.model.AddressModel
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_main_list_item_middle)
open class MainListMiddleView(context: Context) : BaseMainListView(context){

    fun bind(address: AddressModel){
        deliveryTitle.text = address.title
        deliverySubtitle.text = address.subtitle
        adjustLine()
        rightButton.setOnClickListener { listener?.onAddressClicked() }

    }

    open fun adjustLine(){
        preLastLine.visibility = View.GONE
        middleLine.visibility = View.VISIBLE
    }
}