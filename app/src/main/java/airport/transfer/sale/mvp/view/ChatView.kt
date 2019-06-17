package airport.transfer.sale.mvp.view

import com.arellomobile.mvp.MvpView
import airport.transfer.sale.rest.models.response.model.Message


interface ChatView : MvpView{

    fun onMessageSent(message: Message?)

    fun onMessagesReceived(messages: List<Message>)

    fun onFeedbackSent()

    fun onMissedEmail()
}