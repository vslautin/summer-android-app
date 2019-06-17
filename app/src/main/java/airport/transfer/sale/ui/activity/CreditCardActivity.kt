package airport.transfer.sale.ui.activity

import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.order.CreditCardFragment_
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class CreditCardActivity : BaseActivity(){

    @AfterViews
    fun ready(){
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        showFragment(CreditCardFragment_())
    }

}