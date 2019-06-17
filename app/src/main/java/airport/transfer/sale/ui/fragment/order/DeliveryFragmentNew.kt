package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.fragment_address.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.showKeyboard
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.OnActivityResult

@EFragment(R.layout.fragment_address)
open class DeliveryFragmentNew : ArrivalFragment(){

    companion object {
        const val KEY = "key"

        fun newInstance(isAirport: Boolean, previousText: String): DeliveryFragmentNew{
            val args = Bundle()
            args.putBoolean(KEY, isAirport)
            args.putString(Constants.EXTRA_TEXT, previousText)
            val fragment = DeliveryFragmentNew_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMapButton.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ showKeyboard(context, addressEditText) }, 300)
    }

    @OnActivityResult(Constants.REQUEST_ARRIVAL)
    fun onActivityResult(resultCode: Int, data: Intent?){
        if (resultCode == Activity.RESULT_OK){
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
    }
}