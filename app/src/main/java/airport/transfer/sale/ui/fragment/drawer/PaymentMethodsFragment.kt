package airport.transfer.sale.ui.fragment.drawer

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_payment_methods.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.adapter.CreditCardAdapter
import airport.transfer.sale.adapter.ViewHolderWrapper
import airport.transfer.sale.mvp.model.CreditCard
import airport.transfer.sale.mvp.presenter.PaymentPresenter
import airport.transfer.sale.mvp.view.PaymentView
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.CreditCardActivity_
import airport.transfer.sale.ui.activity.PaymentMethodsActivity
import airport.transfer.sale.ui.activity.WebViewActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_payment_methods)
open class PaymentMethodsFragment : BaseFragment(), PaymentView, SwipeCallback.SwipingCallback {

    companion object {
        const val GOOGLE_PAY_ID = -523L
    }

    @InjectPresenter
    lateinit var presenter: PaymentPresenter

    private var realm: Realm? = null
    private lateinit var mAdapter: CreditCardAdapter

    private var mCurrentCardId: Long = 0
    private lateinit var swipeCallback: SwipeCallback

    private var mPaymentClient: PaymentsClient? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        mAdapter = CreditCardAdapter(::onCardClicked)
        bottomText.text = getString(R.string.save)
        addCardButton.setOnClickListener {
            startActivityForResult(Intent(context, CreditCardActivity_::class.java), Constants.REQUEST_PAYMENT)
        }
        setupItemSwipeHelper()
        loadSavedCards()
        saveButton.setOnClickListener {
            Preferences.saveIsLastPaymentGooglePay(context, mCurrentCardId == GOOGLE_PAY_ID)
            if (needGooglePay() && mCurrentCardId == GOOGLE_PAY_ID) {
                chooseGooglePayMethod()
            } else {
                if (mCurrentCardId != 0L) {
                    presenter.defaultCard(Preferences.getAuthToken(context)!!, mCurrentCardId)
                }
            }
        }
        if (needGooglePay()) bottomText.setText(R.string.continue_)
        cardsRecyclerView.layoutManager = LinearLayoutManager(context)
        cardsRecyclerView.adapter = mAdapter
        mAdapter.setClickListener(object : ViewHolderWrapper.ClickListener {
            override fun onItemClicked(position: Int, wrapper: ViewHolderWrapper<*>) {
                onCardClicked(mAdapter.getItem(position).id)
            }
        })
        presenter.getCards(Preferences.getAuthToken(context)!!)
        safePaymentsInfo.setOnClickListener {
            val webIntent = Intent(context, WebViewActivity_::class.java)
            webIntent.putExtra(Constants.EXTRA_KEY, getPaymentLink())
            startActivity(webIntent)
        }
    }

    private fun chooseGooglePayMethod() {
        mPaymentClient = createPaymentsClient(activity)
        presenter.getPublicId(Preferences.getAuthToken(context)!!)
    }

    override fun onPublicIdReceived(publicId: String) {
        val order = getCurrentOrder(realm)
        val readyTask = isReadyToPay(createPaymentsClient(activity))
        readyTask.addOnCompleteListener {
            if (it.result == false) {
                AlertDialog.Builder(context)
                        .setMessage(R.string.no_google_pay_message)
                        .setPositiveButton(R.string.ok, null)
                        .show()
                return@addOnCompleteListener
            }
            val request = createPaymentDataRequest(order.plan?.payPrice?.toString()?.plus(".00")!!, publicId)
            mPaymentClient?.let { AutoResolveHelper.resolveTask(it.loadPaymentData(request), activity, Constants.REQUEST_GPAY) }
        }
    }

    private fun needGooglePay() = activity is PaymentMethodsActivity

    private fun onCardClicked(id: Long) {
        mCurrentCardId = id
        mAdapter.setCheckedId(mCurrentCardId)
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreditCardsReceived(cards: List<CreditCard>) {
        addCards(cards)
        realm?.let {
            if (!it.isClosed) {
                it.executeTransaction {
                    if (cards.isEmpty()) it.delete(CreditCard::class.java)
                    else {
                        it.copyToRealmOrUpdate(cards)
                    }
                }
            }
        }
    }

    private fun loadSavedCards() {
        val cards = realm?.where(CreditCard::class.java)
                ?.sort("createdAt")
                ?.findAll()
        cards?.let { addCards(it) }
    }

    private fun addCards(cards: List<CreditCard>) {
        if (needGooglePay()) {
            mAdapter.add(CreditCard(GOOGLE_PAY_ID, isGooglePay = true))
            swipeCallback.setBlockedPositions(listOf(cards.size))
            if (Preferences.getIsLastPaymentGooglePay(context)) {
                mCurrentCardId = GOOGLE_PAY_ID
                mAdapter.setCheckedId(mCurrentCardId)
                val freeCards = if (cards.getOrNull(0)?.isManaged == true) cards.map { realm!!.copyFromRealm(it) } else cards
                freeCards.forEach { it?.isDefault = false }
                mAdapter.replaceAll(freeCards)
                mAdapter.add(CreditCard(GOOGLE_PAY_ID, isGooglePay = true, isDefault = true))
            } else {
                justAddCards(cards)
                mAdapter.add(CreditCard(GOOGLE_PAY_ID, isGooglePay = true))
            }
        } else justAddCards(cards)
    }

    private fun justAddCards(cards: List<CreditCard>) {
        mCurrentCardId = cards.find { it.isDefault }?.id ?: 0
        mAdapter.setCheckedId(mCurrentCardId)
        mAdapter.replaceAll(cards)
    }

    override fun onDefaultChanged(id: Long) {
        realm?.let {
            if (!it.isClosed) {
                it.executeTransaction {
                    val cards = it.where(CreditCard::class.java).findAll()
                    cards.forEach {
                        it.isDefault = it.id == id
                    }
                }
            }
        }
        if (needGooglePay()) {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        } else {
            context.makeToast(R.string.saved)
            hideKeyboard(context, view?.findFocus())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_PAYMENT -> loadSavedCards()
                /*Constants.REQUEST_GPAY -> {
                   see activity
                }*/
            }
        }
    }

    private fun setupItemSwipeHelper() {
        swipeCallback = SwipeCallback(context, 0, ItemTouchHelper.LEFT)
        swipeCallback.setCallback(this)
        val helper = ItemTouchHelper(swipeCallback)
        helper.attachToRecyclerView(cardsRecyclerView)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder) {
        var delete = false
        AlertDialog.Builder(context)
                .setMessage(R.string.card_delete_confirm)
                .setPositiveButton(R.string.delete, { dialog, which ->
                    delete = true
                    val cardId = mAdapter.getItem(viewHolder.adapterPosition).id
                    presenter.deleteCard(Preferences.getAuthToken(context)!!, cardId, viewHolder.adapterPosition)
                })
                .setNegativeButton(R.string.cancel, null)
                .setOnDismissListener {
                    if (!delete) mAdapter.notifyDataSetChanged()
                }
                .show()
    }

    override fun onCreditCardRemoved(id: Long, adapterPosition: Int, newDefaultId: Long?) {
        mAdapter.removeItem(adapterPosition)
        realm?.let {
            if (!it.isClosed) it.executeTransaction {
                it.where(CreditCard::class.java)?.equalTo("id", id)?.findFirst()?.deleteFromRealm()
            }
        }
        newDefaultId?.let { onCardClicked(it) }
        if (needGooglePay()) swipeCallback.setBlockedPositions(listOf(mAdapter.itemCount - 1))
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}