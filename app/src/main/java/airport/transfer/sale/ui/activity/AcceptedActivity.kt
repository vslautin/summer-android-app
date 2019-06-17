package airport.transfer.sale.ui.activity

import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.order.AcceptedFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class AcceptedActivity : BaseActivity(){

    @AfterViews
    fun ready(){
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        showFragment(AcceptedFragment.getInstance(intent.getBooleanExtra(Constants.EXTRA_FLAG, false)))
    }

}