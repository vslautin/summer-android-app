package airport.transfer.sale.ui.view.chat

import android.content.Context
import kotlinx.android.synthetic.main.view_chat.view.*
import airport.transfer.sale.R
import airport.transfer.sale.fromHtmlCompat
import airport.transfer.sale.mvp.model.ChatModel
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_chat)
open class ChatView(context: Context) : BaseChatView(context){

    fun bind(item: ChatModel){
        message.text = (if (item.fromClient) context.getString(R.string.chat_you)
        else context.getString(R.string.chat_operator)).plus(" ").plus(item.message).fromHtmlCompat()
        if (item.isDelivered) message.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }
}