package airport.transfer.sale.ui.fragment.drawer

import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.R
import airport.transfer.sale.adapter.ChatAdapter
import airport.transfer.sale.mvp.model.ChatModel
import airport.transfer.sale.mvp.presenter.ChatPresenter
import airport.transfer.sale.mvp.view.ChatView
import airport.transfer.sale.rest.models.response.model.Message
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_chat)
open class ChatFragment : BaseFragment(), ChatView{

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    lateinit var chatAdapter: ChatAdapter

    @AfterViews
    fun ready(){
        bottomText.text = getString(R.string.send)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
        chatAdapter = ChatAdapter()
        //val user = Preferences.getUser(context)!!
        /*chatAdapter.addAll(Preferences.getMessages(context).map {
            ChatModel(it.id, it.body, it.createdAt, it.author.id == user.id, true)
        })*/
        chatRecyclerView.adapter = chatAdapter
        presenter.getMessages(Preferences.getAuthToken(context)!!)
        sendMessageButton.setOnClickListener {
            val message = chatAdapter.getFooterView()?.pullMessage()
            message?.let {
                chatAdapter.add(ChatModel(0, it, 100, true, false))
                presenter.sendMessage(it, Preferences.getAuthToken(context)!!)
            }
        }
    }

    override fun onMessageSent(message: Message?) {
        (0..chatAdapter.itemCount - 3).forEach {
            val item = chatAdapter.getItem(it)
            if (item.message == message?.body) {
                item.isDelivered = true
                chatAdapter.setItem(item, it)
            }
        }
    }

    override fun onMessagesReceived(messages: List<Message>) {
        //val user = Preferences.getUser(context)!!
        /*chatAdapter.addAll(messages.sortedBy { it.createdAt }.map {
            ChatModel(it.id, it.body, it.createdAt, it.author.id == user.id, true)
        })
        Preferences.addMessages(messages, context)*/
    }

    override fun onFeedbackSent() {
    }

    override fun onMissedEmail() {
    }
}