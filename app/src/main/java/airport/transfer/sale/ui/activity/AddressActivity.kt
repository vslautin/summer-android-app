package airport.transfer.sale.ui.activity

import android.content.Intent
import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.order.ArrivalFragment
import airport.transfer.sale.ui.fragment.order.DeliveryFragmentNew
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class AddressActivity : BaseActivity(){

    @AfterViews
    fun ready(){
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        val isDestinationAirport = intent.getBooleanExtra(Constants.EXTRA_FLAG, false)
        val text = intent.getStringExtra(Constants.EXTRA_TEXT)
        if (intent.getIntExtra(Constants.EXTRA_KEY, -1) == Constants.REQUEST_ARRIVAL) {
            showFragment(ArrivalFragment.newInstance(!isDestinationAirport, text))
        } else showFragment(DeliveryFragmentNew.newInstance(isDestinationAirport, text))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}