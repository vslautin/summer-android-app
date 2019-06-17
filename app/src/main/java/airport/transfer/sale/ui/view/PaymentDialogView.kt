package airport.transfer.sale.ui.view

import android.content.Context
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_payment_dialog.view.*
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_payment_dialog)
open class PaymentDialogView(context: Context) : RelativeLayout(context) {

    fun bind(callback: (position: Int) -> Unit): PaymentDialogView {
        googlePayText.setOnClickListener { callback.invoke(1) }
        creditCardText.setOnClickListener { callback.invoke(0) }
        return this
    }
}