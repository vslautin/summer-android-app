package airport.transfer.sale.ui.view.order

import android.content.Context
import kotlinx.android.synthetic.main.view_main_list_item_initial.view.*
import airport.transfer.sale.R
import airport.transfer.sale.mvp.model.AddressModel
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_main_list_item_initial)
open class MainListInitialView(context: Context) : BaseMainListView(context) {

    fun bind(model: AddressModel){
        arrivalTitle.text = model.title
        arrivalSubtitle.text = model.subtitle
    }
}