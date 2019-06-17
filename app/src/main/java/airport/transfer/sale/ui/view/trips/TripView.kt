package airport.transfer.sale.ui.view.trips

import android.content.Context
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.view_trip.view.*
import airport.transfer.sale.R
import airport.transfer.sale.adapter.BaseTripViewHolder
import airport.transfer.sale.fromServerFormat
import airport.transfer.sale.rest.models.response.model.v2.Order
import org.androidannotations.annotations.EViewGroup
import java.text.SimpleDateFormat
import java.util.*

@EViewGroup(R.layout.view_trip)
open class TripView(context: Context) : BaseTripView(context){

    fun bind(order: Order){
        order.deliveryTime?.let{ tripDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(Date(it.fromServerFormat()!!)) }
        arrivalTitle.text = order.delivery?.title
        deliveryTitle.text = order.destination?.title
        tripPrice.text = order.viewPrice
        serviceClass.text = order.plan?.title
        orderNumber.text = order.code
        fulfillInfo(order)
        nextTripView.setOnClickListener { listener?.onTripClicked(null) }
        leftButton.setOnClickListener { listener?.onTripClicked(BaseTripViewHolder.ButtonType.REPEAT) }
        rightButton.setOnClickListener { listener?.onTripClicked(BaseTripViewHolder.ButtonType.DELETE) }
        collapseDetailView()
        collapseButtonsView()
    }

    fun expandDetailView(){
        detailTripView.visibility = View.VISIBLE
    }

    fun collapseDetailView(){
        detailTripView.visibility = View.GONE
    }

    fun expandButtonsView(){
        buttonsView.visibility = View.VISIBLE
    }

    fun collapseButtonsView(){
        buttonsView.visibility = View.GONE
    }

    fun fulfillInfo(order: Order){
        detailsLayout.removeAllViews()
        val list = ArrayList<Pair<Int, String?>>()
        with(list){
            add(Pair(if (order.delivery?.isAirport == true) R.string.arrival_airport
                    else R.string.origin_address, order.delivery?.title))
            add(Pair(if (order.destination?.isAirport == true) R.string.destination_airport
                    else R.string.destination_address, order.destination?.title))
            add(Pair(R.string.payment_method, order.creditCard?.getTitle()))
            add(Pair(R.string.service_class, order.plan?.title))
            add(Pair(R.string.child_seat, if (order.babyChair == 0) context.getString(R.string.no) else order.babyChair.toString()))
            add(Pair(R.string.transfer_cost, order.viewPrice))
        }
        list.forEach {
            val info = View.inflate(context, R.layout.view_order_info_item, null)
            info.findViewById<TextView>(R.id.orderInfoName).text = context.getString(it.first)
            info.findViewById<TextView>(R.id.orderInfoValue).text = it.second
            detailsLayout.addView(info)
        }
    }
}