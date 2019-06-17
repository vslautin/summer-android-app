package airport.transfer.sale.ui.view.order

import android.content.Context
import android.widget.RelativeLayout


abstract class BaseMainListView(context: Context) : RelativeLayout(context){

    protected var listener: AddressClickListener? = null

    fun setClickListener(clickListener: AddressClickListener) {
        listener = clickListener
    }

    interface AddressClickListener{
        fun onAddressClicked()
    }
}