package airport.transfer.sale.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.ui.view.trips.*
import java.util.*


open class TripAdapter : RecyclerView.Adapter<BaseTripViewHolder>() {

    protected val HEADER_VIEW = 2
    protected val PLACEHOLDER_VIEW = 3
    var items: MutableList<Order> = ArrayList<Order>()
    protected var clickListener: BaseTripViewHolder.ClickListener? = null

    override fun onBindViewHolder(holder: BaseTripViewHolder?, position: Int) {
        val currentOrdersQuantity = items.count { it.status != 6 }
        if (currentOrdersQuantity == 0) {
            when (position) {
                0 -> (holder as HeaderTripViewHolder).v.bind(R.string.next_trip)
                1 -> (holder as PlaceholderTripViewHolder).v.bind(Constants.OrderTense.CURRENT)
                2 -> (holder as HeaderTripViewHolder).v.bind(R.string.history_list)
                else -> {
                    if (holder is PlaceholderTripViewHolder) holder.v.bind(Constants.OrderTense.COMPLETED)
                    else (holder?.view as TripView).bind(items[position - 3])
                }
            }
        } else {
            when (position) {
                0 -> (holder as HeaderTripViewHolder).v.bind(R.string.next_trip)
                in (1..currentOrdersQuantity) -> {
                    (holder?.view as TripView).bind(items.filter { it.status != 6 }[position - 1])
                }
                currentOrdersQuantity + 1 -> (holder as HeaderTripViewHolder).v.bind(R.string.history_list)
                else -> {
                    if (holder is PlaceholderTripViewHolder) holder.v.bind(Constants.OrderTense.COMPLETED)
                    else (holder?.view as TripView).bind(items[position - 2])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        val completedItemsCount = items.count { it.status == 6 }
        val currentItemsCount = items.count { it.status != 6 }
        var placeholdersCount = 0
        if (completedItemsCount == 0) placeholdersCount++
        if (currentItemsCount == 0) placeholdersCount++
        return items.size + 2 + placeholdersCount
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
        val currentOrdersQuantity = items.count { it.status != 6 }
        val secondTitlePosition = if (currentOrdersQuantity == 0) 2 else currentOrdersQuantity + 1
        when (position) {
            0 -> return HEADER_VIEW
            1 -> {
                if (currentOrdersQuantity == 0) return PLACEHOLDER_VIEW
                else return super.getItemViewType(position)
            }
            secondTitlePosition -> return HEADER_VIEW
            secondTitlePosition + 1 -> {
                if (items.count { it.status == 6 } == 0) return PLACEHOLDER_VIEW
                else return super.getItemViewType(position)
            }
            else -> return super.getItemViewType(position)
        }
    }

    fun setItemClickListener(listener: BaseTripViewHolder.ClickListener) {
        clickListener = listener
    }

    private fun sortItems(){
        items = items.sortedBy { it.status }.toMutableList()
    }

    fun getCurrentOrdersQuantity() = items.count { it.status != 6 }

    fun addAll(items: List<Order>) {
        items.forEach{ addedItem ->
            val same = this.items.find { existedItem ->
                existedItem.id == addedItem.id
            }
            same?.let { this.items.remove(same) }
        }
        this.items.addAll(items)
        sortItems()
        notifyDataSetChanged()
    }

    fun replaceAll(items: List<Order>) {
        this.items.clear()
        this.items.addAll(items)
        sortItems()
        notifyDataSetChanged()
    }

    fun add(item: Order) {
        items.add(item)
        sortItems()
        notifyItemInserted(items.size + 1)
    }

    fun removeItem(position: Int): Order {
        val removedItem = getItem(position)
        items.remove(removedItem)
        notifyItemRemoved(position)
        return removedItem
    }

    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    open fun getItem(position: Int): Order {
        val currentOrdersQuantity = items.count { it.status != 6 }
        if (currentOrdersQuantity == 0) return items[position - 3]
        if (position <= currentOrdersQuantity) return items[position - 1]
        return items[position - 2]
    }

}

open class BaseTripViewHolder(val view: BaseTripView, val listener: BaseTripViewHolder.ClickListener?) : RecyclerView.ViewHolder(view) {

    enum class ButtonType{ REPEAT, DELETE }

    init {
        view.setClickListener(object : BaseTripView.TripClickListener {
            override fun onTripClicked(buttonType: ButtonType?) {
                listener?.onItemClicked(adapterPosition, this@BaseTripViewHolder, buttonType)
            }
        })
    }

    interface ClickListener {
        fun onItemClicked(position: Int, holder: BaseTripViewHolder, buttonType: ButtonType?)
    }
}

class TripViewHolder(val v: TripView, l: BaseTripViewHolder.ClickListener?) : BaseTripViewHolder(v, l)

class HeaderTripViewHolder(val v: HeaderTripsView, l: BaseTripViewHolder.ClickListener?) : BaseTripViewHolder(v, l)

class PlaceholderTripViewHolder(val v: PlaceholderTripView, val l: BaseTripViewHolder.ClickListener?) : BaseTripViewHolder(v, l)