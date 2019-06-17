package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.ui.view.trips.*


class FavoriteTripsAdapter : TripAdapter(){

    override fun onBindViewHolder(holder: BaseTripViewHolder?, position: Int) {
        when (holder) {
            is TripViewHolder -> (holder.view as TripView).bind(items[position])
            is HeaderTripViewHolder -> holder.v.bind(R.string.favorite_trips)
            is PlaceholderTripViewHolder -> holder.v.bind(Constants.OrderTense.FAVORITE)
        }
    }

    override fun getItemCount(): Int {
        if (items.size == 0) return 1
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTripViewHolder? {
        when (viewType) {
            HEADER_VIEW -> {
                return HeaderTripViewHolder(HeaderTripsView_.build(parent.context), clickListener)
            }
            PLACEHOLDER_VIEW -> {
                return PlaceholderTripViewHolder(PlaceholderTripView_.build(parent.context), clickListener)
            }
            else -> return TripViewHolder(TripView_.build(parent.context), clickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> {
                if (items.size == 0) return PLACEHOLDER_VIEW
                else return 0
            }
            else -> return 0
        }
    }

    override fun getItem(position: Int): Order {
        return items[position]
    }
}