package airport.transfer.sale.ui.view

import android.content.Context
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_credit_card.view.*
import airport.transfer.sale.R
import airport.transfer.sale.boldFirstWord
import airport.transfer.sale.mvp.model.CreditCard
import android.view.View
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_credit_card)
open class CreditCardView(context: Context) : RelativeLayout(context) {

    fun bind(card: CreditCard, currentCheckedId: Long?, listener: (Long) -> Unit) {
        radioImage.setOnCheckedChangeListener(null)
        titleText.text = card.getTitle().boldFirstWord()
        if (currentCheckedId == null) radioImage.isChecked = card.isDefault
        else radioImage.isChecked = card.id == currentCheckedId
        radioImage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) listener.invoke(card.id)
        }
        if (card.isGooglePay) {
            gPayImage.visibility = View.VISIBLE
            titleText.text = context.getString(R.string.google_pay)
        } else gPayImage.visibility = View.GONE
    }
}