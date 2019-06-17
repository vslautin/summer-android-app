package airport.transfer.sale.ui.view

import android.content.Context
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_car_price_item.view.*
import airport.transfer.sale.R
import airport.transfer.sale.mvp.model.CarPriceModel
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_car_price_item)
open class CarPriceView(context: Context) : RelativeLayout(context){

    fun bind(item: CarPriceModel){
        title.text = item.carClass
        description.text = item.carPrices
    }
}