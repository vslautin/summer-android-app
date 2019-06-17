package airport.transfer.sale.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import airport.transfer.sale.mvp.model.AddressModel
import airport.transfer.sale.ui.view.order.*


class OrderListAdapter : RecyclerView.Adapter<AddressViewHolder>() {

    private val HEADER_VIEW = 0
    private val FOOTER_VIEW = 1
    private val MIDDLE_VIEW = 2
    private val PRE_LAST_VIEW = 3
    private val items = ArrayList<AddressModel>()
    private var itemClickListener: AddressViewHolder.ClickListener? = null

    override fun onBindViewHolder(holder: AddressViewHolder?, position: Int) {
        when (holder) {
            is InitialViewHolder -> (holder.view as MainListInitialView).bind(items[position])
            is MiddleViewHolder -> (holder.view as MainListMiddleView).bind(items[position])
            is PreLastViewHolder -> (holder.view as MainListPreLastView).bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder? {
        when (viewType) {
            HEADER_VIEW -> return InitialViewHolder(MainListInitialView_.build(parent.context), itemClickListener)
            FOOTER_VIEW -> return LastViewHolder(MainListLastView_.build(parent.context), itemClickListener)
            MIDDLE_VIEW -> return MiddleViewHolder(MainListMiddleView_.build(parent.context), itemClickListener)
            PRE_LAST_VIEW -> return PreLastViewHolder(MainListPreLastView_.build(parent.context), itemClickListener)
        }
        return null
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return HEADER_VIEW
            items.size -> return FOOTER_VIEW
            items.size - 1 -> return PRE_LAST_VIEW
            else -> return MIDDLE_VIEW
        }
    }

    fun setItemClickListener(listener: AddressViewHolder.ClickListener){
        itemClickListener = listener
    }

    fun addAll(items: List<AddressModel>) {
        val positionStart = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
        notifyItemChanged(items.size) // for bind total
    }

    fun removeItem(position: Int): AddressModel{
        val removedItem = items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(items.size) // for bind total
        return removedItem
    }

    fun getItem(position: Int) = items[position]

}

open class AddressViewHolder(val view: BaseMainListView, val listener: AddressViewHolder.ClickListener?) : RecyclerView.ViewHolder(view) {

    init {
        view.setClickListener(object : BaseMainListView.AddressClickListener{
            override fun onAddressClicked() {
                listener?.onItemClicked(adapterPosition)
            }
        })
    }

    interface ClickListener{
        fun onItemClicked(position: Int)
    }
}

class InitialViewHolder(val v: MainListInitialView, val l: AddressViewHolder.ClickListener?) : AddressViewHolder(v, l)

class MiddleViewHolder(val v: MainListMiddleView, val l: AddressViewHolder.ClickListener?) : AddressViewHolder(v, l)

class PreLastViewHolder(val v: MainListPreLastView, val l: AddressViewHolder.ClickListener?) : AddressViewHolder(v, l)

class LastViewHolder(val v: MainListLastView, val l: AddressViewHolder.ClickListener?) : AddressViewHolder(v, l)