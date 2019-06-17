package airport.transfer.sale.ui.view.trips

import android.content.Context
import kotlinx.android.synthetic.main.view_trips_header.view.*
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup
@EViewGroup(R.layout.view_trips_header)
open class HeaderTripsView(context: Context) : BaseTripView(context){

    fun bind(titleResId: Int){
        headerTitle.text = context.getString(titleResId)
    }
}