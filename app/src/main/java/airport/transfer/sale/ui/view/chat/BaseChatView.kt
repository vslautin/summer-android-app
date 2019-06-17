package airport.transfer.sale.ui.view.chat

import android.content.Context
import android.widget.RelativeLayout


abstract class BaseChatView(context: Context) : RelativeLayout(context) {

    protected var listener: ChatClickListener? = null

    fun setClickListener(clickListener: ChatClickListener) {
        listener = clickListener
    }

    interface ChatClickListener{
        fun onMessageClicked()
    }
}