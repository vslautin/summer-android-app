package airport.transfer.sale.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import airport.transfer.sale.mvp.model.ChatModel
import airport.transfer.sale.ui.view.chat.*


class ChatAdapter : RecyclerView.Adapter<BaseChatViewHolder>() {

    private val HEADER_VIEW = 2
    private val FOOTER_VIEW = 1
    private var items: MutableList<ChatModel> = ArrayList<ChatModel>()
    private var itemClickListener: BaseChatViewHolder.ClickListener? = null
    private var footerView: FooterChatView? = null

    override fun onBindViewHolder(holder: BaseChatViewHolder?, position: Int) {
        when (holder) {
            is ChatViewHolder -> (holder.view as ChatView).bind(items[position - 1])
        }
    }

    override fun getItemCount(): Int = items.size + 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder? {
        when (viewType) {
            HEADER_VIEW -> return HeaderChatViewHolder(HeaderChatView_.build(parent.context), itemClickListener)
            FOOTER_VIEW -> {
                footerView = FooterChatView_.build(parent.context)
                footerView?.let { return FooterChatViewHolder(it, itemClickListener) }
                return null
            }
            else -> return ChatViewHolder(ChatView_.build(parent.context), itemClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return HEADER_VIEW
            items.size + 1 -> return FOOTER_VIEW
            else -> return super.getItemViewType(position)
        }
    }

    fun setItemClickListener(listener: BaseChatViewHolder.ClickListener){
        itemClickListener = listener
    }

    fun addAll(items: List<ChatModel>) {
        val newItems = this.items.toMutableSet()
        newItems.addAll(items)
        this.items = newItems.sortedBy { it.timestamp }.toMutableList()
        notifyDataSetChanged()
    }

    fun add(item: ChatModel){
        items.add(item)
        notifyItemInserted(items.size + 1)
    }

    fun removeItem(position: Int): ChatModel{
        val removedItem = items.removeAt(position)
        notifyItemRemoved(position)
        return removedItem
    }

    fun getItem(position: Int) = items[position]

    fun setItem(item: ChatModel, position: Int) {
        items[position] = item
        notifyItemChanged(position + 1)//because of header
    }

    fun getFooterView() = footerView

}

open class BaseChatViewHolder(val view: BaseChatView, val listener: BaseChatViewHolder.ClickListener?) : RecyclerView.ViewHolder(view) {

    init {
        view.setClickListener(object : BaseChatView.ChatClickListener{
            override fun onMessageClicked() {
                listener?.onItemClicked(adapterPosition)
            }
        })
    }

    interface ClickListener{
        fun onItemClicked(position: Int)
    }
}

class FooterChatViewHolder(val v: FooterChatView, val l: BaseChatViewHolder.ClickListener?) : BaseChatViewHolder(v, l)

class ChatViewHolder(val v: ChatView, val l: BaseChatViewHolder.ClickListener?) : BaseChatViewHolder(v, l)

class HeaderChatViewHolder(val v: HeaderChatView, val l: BaseChatViewHolder.ClickListener?) : BaseChatViewHolder(v, l)