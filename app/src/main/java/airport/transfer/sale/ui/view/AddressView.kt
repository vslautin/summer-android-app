package airport.transfer.sale.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_address_item.view.*
import airport.transfer.sale.R
import airport.transfer.sale.rest.models.response.model.v2.Address
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_address_item)
open class AddressView(context: Context) : RelativeLayout(context){

    fun bind(address: Address) {
        addressTitle.text = address.title
        addressSubtitle.text = address.subtitle
        if (address.isAirport == false) planeImage.visibility = View.GONE
        else planeImage.visibility = View.VISIBLE
    }
}