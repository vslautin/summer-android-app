package airport.transfer.sale.ui.view.chat

import android.content.Context
import kotlinx.android.synthetic.main.view_chat_footer.view.*
import airport.transfer.sale.R
import org.androidannotations.annotations.EViewGroup

@EViewGroup(R.layout.view_chat_footer)
open class FooterChatView(context: Context) : BaseChatView(context){

    fun setInitial(){
        headerTitle.text = context.getString(R.string.write_to_us)
        chatInput.hint = context.getString(R.string.write_hint)
    }

    fun pullMessage(): String {
        val m = chatInput.text.toString()
        chatInput.setText("")
        return m
    }
}