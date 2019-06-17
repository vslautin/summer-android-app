package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_rate.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.hideKeyboard
import airport.transfer.sale.mvp.presenter.OrderPresenter
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Review
import airport.transfer.sale.rest.models.response.model.v2.Transaction
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.fragment.drawer.TransferFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_rate)
open class RateFragment : BaseFragment(), OrderView{

    @InjectPresenter
    lateinit var editOrderPresenter: OrderPresenter

    lateinit var realm: Realm

    private var mFeedbackObservable: Observable<Boolean>? = null

    companion object {

        fun newInstance(orderJson: String): RateFragment {
            val args = Bundle()
            args.putString(Constants.EXTRA_KEY, orderJson)
            val fragment = RateFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        bottomText.text = getString(R.string.send)
        val order = Gson().fromJson(arguments.getString(Constants.EXTRA_KEY), Order::class.java)
        order.isReviewProposed = true
        realm.executeTransaction {
            it.copyToRealmOrUpdate(order)
        }
        editOrderPresenter.editOrder(Preferences.getAuthToken(context)!!, order)
        sendButton.setOnClickListener {
            val text = rateCommentText.text.toString()
            order.review = Review(rate = ratingView.rating.toInt(), comment = if (TextUtils.isEmpty(text)) "" else text)
            editOrderPresenter.addFeedback(Preferences.getAuthToken(context)!!, order)
            realm.executeTransaction {
                it.copyToRealmOrUpdate(order)
            }
            hideKeyboard(context, rateCommentText)
            (parentFragment as TransferFragment).popBackStack(true, false)
        }
        mFeedbackObservable = RxTextView.textChanges(rateCommentText).observeOn(AndroidSchedulers.mainThread()).map { it.isNotEmpty() }
        sendButton?.isEnabled = false
        mFeedbackObservable?.subscribe {
            sendButton?.isEnabled = it
            bottomText?.isEnabled = it
        }
    }

    override fun onCurrentStateReceived(edit: Boolean, back: Boolean) {
    }

    override fun onOrderCreated(order: Order, transaction: Transaction?) {
    }

    override fun onOrderCancelled(positionToRemove: Int) {
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        activity.setResult(Activity.RESULT_FIRST_USER)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        mFeedbackObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
    }
}