package airport.transfer.sale.ui.fragment.drawer

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.makeToast
import airport.transfer.sale.mvp.presenter.ChatPresenter
import airport.transfer.sale.mvp.view.ChatView
import airport.transfer.sale.rest.models.response.model.Message
import airport.transfer.sale.showKeyboard
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_feedback)
open class FeedbackFragment : BaseFragment(), ChatView{

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    companion object {
        fun newInstance(orderId: Long): FeedbackFragment{
            val args = Bundle()
            args.putLong(Constants.EXTRA_KEY, orderId)
            val fragment = FeedbackFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    @AfterViews
    fun ready(){
        bottomText.setText(R.string.send)
        sendButton.setOnClickListener {
            val token = Preferences.getAuthToken(context)
            if (token == null) activity.makeToast(R.string.please_authorize)
            else {
                if (chatInput.text.isNotEmpty()) {
                    presenter.sendFeedback(chatInput.text.toString(), token)
                    sendButton.isEnabled = false
                    bottomText.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ showKeyboard(context, chatInput) }, 500)
    }

    override fun onMessageSent(message: Message?) {
    }

    override fun onMessagesReceived(messages: List<Message>) {
    }

    override fun onFeedbackSent() {
        sendButton.isEnabled = true
        bottomText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        chatInput.setText("")
        AlertDialog.Builder(context)
                .setMessage(R.string.sent_success)
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    override fun onMissedEmail() {
        activity.makeToast(R.string.fill_email)
    }
}