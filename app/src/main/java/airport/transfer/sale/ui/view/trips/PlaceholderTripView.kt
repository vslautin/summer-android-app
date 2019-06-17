package airport.transfer.sale.ui.view.trips

import android.content.Context
import kotlinx.android.synthetic.main.view_trip_placeholder.view.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_trip_placeholder)
open class PlaceholderTripView(context: Context): BaseTripView(context) {

    fun bind(tense: Constants.OrderTense){
        when (tense) {
            Constants.OrderTense.COMPLETED -> placeholderTitle.text = context.getString(R.string.no_completed_orders)
            Constants.OrderTense.CURRENT -> placeholderTitle.text = context.getString(R.string.no_current_order)
            Constants.OrderTense.FAVORITE -> placeholderTitle.text = context.getString(R.string.favorites_placeholder)
        }
    }
}