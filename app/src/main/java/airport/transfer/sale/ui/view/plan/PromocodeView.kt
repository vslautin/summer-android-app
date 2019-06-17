package airport.transfer.sale.ui.view.plan

import airport.transfer.sale.R
import android.content.Context
import android.widget.EditText
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_promocode.view.*
import org.androidannotations.annotations.EViewGroup
import java.util.*

@EViewGroup(R.layout.view_promocode)
open class PromocodeView(context: Context): RelativeLayout(context) {

    fun bind(discount: Int/*, listener: (EditText) -> Unit*/){
        if (discount == 0) {
            promoSaleLabel.visibility = GONE
            promoSaleText.visibility = GONE
            percentText.visibility = GONE
        } else {
            promoSaleLabel.visibility = VISIBLE
            promoSaleText.visibility = VISIBLE
            percentText.visibility = VISIBLE
            promoSaleText.text = String.format(Locale.getDefault(), "%s :", promoEditText.text.toString())
            percentText.text = String.format(Locale.getDefault(), "-%d%%", discount)
        }
        //listener.invoke(promoEditText)
    }
}