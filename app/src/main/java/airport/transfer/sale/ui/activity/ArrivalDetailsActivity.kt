package airport.transfer.sale.ui.activity

import kotlinx.android.synthetic.main.toolbar.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.ui.fragment.order.ArrivalDetailsFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class ArrivalDetailsActivity : BaseActivity() {

    @AfterViews
    fun ready(){
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        val request = intent.getIntExtra(Constants.EXTRA_KEY, -1)
        showFragment(ArrivalDetailsFragment.newInstance(request == Constants.REQUEST_DESTINATION_FLIGHT,
                if (intent.hasExtra(Constants.EXTRA_TEXT)) intent.getStringExtra(Constants.EXTRA_TEXT) else ""))
    }

}