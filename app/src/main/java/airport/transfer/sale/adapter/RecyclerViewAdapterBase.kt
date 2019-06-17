package airport.transfer.sale.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.*


abstract class RecyclerViewAdapterBase<T, V : View> : RecyclerView.Adapter<ViewHolderWrapper<V>>() {

    protected var items: MutableList<T> = ArrayList()
    private var clickListener: ViewHolderWrapper.ClickListener? = null

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWrapper<V> {
        return ViewHolderWrapper(onCreateItemView(parent, viewType), clickListener)
    }

    internal abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): V

    fun init(items: MutableList<T>, clickListener: ViewHolderWrapper.ClickListener) {
        this.items = items
        this.clickListener = clickListener
    }

    fun setClickListener(clickListener: ViewHolderWrapper.ClickListener) {
        this.clickListener = clickListener
    }

    fun addAll(items: List<T>) {
        val positionStart = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun addAll(location: Int, items: List<T>) {
        this.items.addAll(location, items)
        notifyItemRangeInserted(location, items.size)
    }

    open fun replaceAll(items: List<T>) {
        removeAll()
        addAll(items)
    }

    fun add(item: T): Int {
        items.add(item)
        notifyItemInserted(itemCount - 1)
        return itemCount - 1
    }

    fun add(position: Int, item: T): Int {
        items.add(position, item)
        notifyItemInserted(position)
        return itemCount - 1
    }

    fun removeItem(position: Int): T? {
        if (items.size > position) {
            val item = items.removeAt(position)
            notifyItemRemoved(position)
            return item
        }
        return null
    }

    fun removeAll() {
        this.items.clear()
        notifyDataSetChanged()
    }

    open fun getItem(position: Int): T {
        return items[position]
    }

    fun existItem(item: T): Boolean {
        return items.contains(item)
    }

    fun getItemPosition(item: T): Int {
        if (!existItem(item)) return -1
        return items.indexOf(item)
    }

    fun setItem(item: T, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

}

class ViewHolderWrapper<out V : View>(val view: V, private val clickListener: ViewHolderWrapper.ClickListener?) : RecyclerView.ViewHolder(view), View.OnClickListener {

    init {
        view.setOnClickListener(this)
    }

    interface ClickListener {
        fun onItemClicked(position: Int, wrapper: ViewHolderWrapper<*>)
    }

    override fun onClick(v: View) {
        clickListener?.onItemClicked(adapterPosition, this)
    }
}
