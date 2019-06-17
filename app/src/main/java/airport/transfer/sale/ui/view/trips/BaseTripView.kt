package airport.transfer.sale.ui.view.trips

import android.content.Context
import android.widget.RelativeLayout
import airport.transfer.sale.adapter.BaseTripViewHolder


abstract class BaseTripView(context: Context) : RelativeLayout(context) {

    protected var listener: TripClickListener? = null

    fun setClickListener(clickListener: TripClickListener) {
        listener = clickListener
    }

    interface TripClickListener{
        fun onTripClicked(buttonType: BaseTripViewHolder.ButtonType?)
    }
}