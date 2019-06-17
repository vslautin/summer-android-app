package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.wallet.*
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_accepted.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.R
import airport.transfer.sale.firebase.MessagingService
import airport.transfer.sale.mvp.model.CreditCard
import airport.transfer.sale.mvp.presenter.OrderPresenter
import airport.transfer.sale.mvp.presenter.PaymentPresenter
import airport.transfer.sale.mvp.view.OrderView
import airport.transfer.sale.mvp.view.PaymentView
import airport.transfer.sale.rest.models.response.model.User
import airport.transfer.sale.rest.models.response.model.v2.Order
import airport.transfer.sale.rest.models.response.model.v2.Transaction
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.CreditCardActivity_
import airport.transfer.sale.ui.activity.MainActivity_
import airport.transfer.sale.ui.activity.WebViewActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import java.util.*

@EFragment(R.layout.fragment_accepted)
open class AcceptedFragment : BaseFragment(), OrderView, PaymentView {

    @InjectPresenter
    lateinit var presenter: OrderPresenter

    @InjectPresenter
    lateinit var paymentPresenter: PaymentPresenter

    companion object {
        const val KEY = "key"

        fun getInstance(showInfo: Boolean): AcceptedFragment {
            val args = Bundle()
            args.putBoolean(KEY, showInfo)
            val fragment = AcceptedFragment_.builder().build()
            fragment.arguments = args
            return fragment
        }
    }

    private var realm: Realm? = null

    private var cachedDefaultCardId = 0L
    private var mOrderToPay: Order? = null

    private var mPaymentClient: PaymentsClient? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        bottomText.text = getString(R.string.thanks_for_choose_transfer)
        fulfillInfo()
        editButton.setOnClickListener {
            //(parentFragment as TransferFragment).popBackStack(false, false)
            activity.finish()
        }
        thanksButton.setOnClickListener {
            if (Preferences.getIsLastPaymentGooglePay(context)) {
                val token = Preferences.getGooglePayToken(context)
                presenter.createOrder(Preferences.getAuthToken(context)!!, getCurrentOrder(realm), token)

//                mPaymentClient = createPaymentsClient(activity)
//                mPaymentClient?.let {
//                    isReadyToPay(it).addOnCompleteListener {
//                        if (it.result) {
//                            var dialog: AlertDialog? = null
//                            dialog = context.showPaymentDialog(context, { position ->
//                                if (position == 0) {
//                                    tryPayCard()
//                                } else {
//                                    paymentPresenter.getPublicId(Preferences.getAuthToken(context)!!)
//                                }
//                                dialog?.dismiss()
//                            })
//                        } else tryPayCard()
//                    }
//                }
            } else tryPayCard()
        }
        paymentPresenter.getCards(Preferences.getAuthToken(context)!!)
    }

    private fun tryPayCard() {
        if (isNoCreditCards()) {
            Toast.makeText(context, R.string.should_add_card, Toast.LENGTH_LONG).show()
            startActivityForResult(Intent(context, CreditCardActivity_::class.java), Constants.REQUEST_CREDIT_CARDS)
        } else {
            presenter.createOrder(Preferences.getAuthToken(context)!!, getCurrentOrder(realm))
        }
    }
/*
    override fun onPublicIdReceived(publicId: String) {
        val order = getCurrentOrder(realm)
        val readyTask = isReadyToPay(createPaymentsClient(activity))
        readyTask.addOnCompleteListener {
            if (it.result == false) return@addOnCompleteListener
            val request = createPaymentDataRequest(order.plan?.payPrice?.toString()?.plus(".00")!!, publicId)
            mPaymentClient?.let { AutoResolveHelper.resolveTask(it.loadPaymentData(request), activity, Constants.REQUEST_GPAY) }
        }
    }*/

    private fun isNoCreditCards() = realm?.where(CreditCard::class.java)?.findAll()?.isEmpty() == true

    override fun onCurrentStateReceived(edit: Boolean, back: Boolean) {
    }

    override fun onCreditCardsReceived(cards: List<CreditCard>) {
        if (cards.isEmpty()) {
            realm?.executeTransaction {
                if (!it.isClosed) it.delete(CreditCard::class.java)
            }
        } else {
            if (cachedDefaultCardId != cards.find { it.isDefault }?.id) {
                realm?.executeTransaction {
                    if (!it.isClosed) it.copyToRealmOrUpdate(cards)
                }
                infoLayout.removeAllViews()
                fulfillInfo()
            }
        }
    }

    private var backTransfer = false

    override fun onOrderCreated(order: Order, transaction: Transaction?) {
        if (transaction?.status == false) {
            if (!transaction.message.isNullOrEmpty()) {
                Toast.makeText(context, transaction.message, Toast.LENGTH_LONG).show()
            } else {
                val webIntent = Intent(context, WebViewActivity_::class.java)
                val postParams = "MD=${transaction.TransactionId}&PaReq=${transaction.PaReq}&TermUrl=${transaction.TermUrl}"
                webIntent.putExtra(Constants.EXTRA_KEY, transaction.AcsUrl)
                webIntent.putExtra(Constants.EXTRA_POST, postParams)
                startActivityForResult(webIntent, Constants.REQUEST_PAYMENT)
                mOrderToPay = order
            }
        } else {
            showOrderNotification(order)
            if (arguments.getBoolean(KEY)) {
                val user = getUser(realm)
                showInfoDialog(getUserName(user), order.delivery?.isAirport == true)
            } else {
                saveCurrentOrder(realm, Order())
                //(parentFragment as TransferFragment).popBackStack(true, false)
                AlertDialog.Builder(activity)
                        .setTitle(R.string.order_accepted)
                        .setMessage(
                                if (order.delivery?.isAirport == true) R.string.confirm_driver_explanation
                                else R.string.hotel_explanation
                        )
                        .setPositiveButton(R.string.ok, null)
                        .setOnDismissListener {
                            activity.setResult(Activity.RESULT_OK)
                            activity.finish()
                        }
                        .show()
            }
        }
    }

    private fun getUserName(user: User?): String {
        return if (user?.lastName.isNullOrEmpty()) {
            if (user?.firstName.isNullOrEmpty()) "TRANSFER"
            else user?.firstName!!
        } else user?.lastName!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_PAYMENT -> {
                    onOrderCreated(mOrderToPay ?: Order(), Transaction(status = true))
                }
                Constants.REQUEST_GPAY -> {
                    data?.let {
                        val paymentData = PaymentData.getFromIntent(it)
                        val token = paymentData?.paymentMethodToken?.token
                        val availableFields = "Available data: email=${paymentData?.email} userAddressName=${paymentData?.shippingAddress?.name} " +
                                "cardDescription=${paymentData?.cardInfo?.cardDescription} cardDetails=${paymentData?.cardInfo?.cardDetails} " +
                                "cardNetwork=${paymentData?.cardInfo?.cardNetwork} cardClass=${paymentData?.cardInfo?.cardClass} " +
                                "billingAddressName=${paymentData?.cardInfo?.billingAddress?.name}"
                        AlertDialog.Builder(context).setMessage(availableFields).setPositiveButton("OK", null).show()
                        presenter.createOrder(Preferences.getAuthToken(context)!!, getCurrentOrder(realm), token)
                    }
                }
                Constants.REQUEST_CREDIT_CARDS -> tryPayCard()
            }
        } else if (resultCode == AutoResolveHelper.RESULT_ERROR) {
            val status = AutoResolveHelper.getStatusFromIntent(data)
            System.out.println("Google Pay result $status")
        }
        data?.let {
            val errorMessage = it.getStringExtra(Constants.EXTRA_KEY)
            if (errorMessage != null) AlertDialog.Builder(context)
                    .setMessage(errorMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show()
        }
    }

    private fun showInfoDialog(userName: String, isAirportOrigin: Boolean) {
        val dialog = showInfoPage(context, R.layout.view_info_confirm)
        dialog.findViewById<TextView>(R.id.backTransferButton).setOnClickListener {
            backTransfer = true
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.closeButton).setOnClickListener {
            backTransfer = false
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.nameText).text = userName
        dialog.findViewById<TextView>(R.id.explanationText).setText(
                if (isAirportOrigin) R.string.confirm_driver_explanation
                else R.string.hotel_explanation)
        dialog.setOnDismissListener {
            if (!backTransfer) saveCurrentOrder(realm, Order())
            //(parentFragment as TransferFragment).popBackStack(!backTransfer, backTransfer)
            val data = Intent()
            data.putExtra(Constants.EXTRA_KEY, backTransfer)
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
        dialog.show()
    }

    override fun onOrderCancelled(positionToRemove: Int) {
    }

    private fun fulfillInfo() {
        val order = getCurrentOrder(realm)
        val list = ArrayList<Pair<Int, String?>>()
        with(list) {
            add(Pair(R.string.arrival_airport, order.delivery?.title))
            add(Pair(R.string.pickup_date_time, order.deliveryTime?.fromServerToUserDate(context)))
            add(Pair(R.string.destination_address, order.destination?.title))
            add(Pair(R.string.payment_method, getDefaultPayment() ?: "-"))
            add(Pair(R.string.service_class, order.plan?.title))
            add(Pair(R.string.child_seat, if (order.babyChair == 0) context.getString(R.string.no) else order.babyChair.toString()))
            //if (!TextUtils.isEmpty(order.comment)) add(Pair(R.string.order_comment, order.comment))
            add(Pair(R.string.transfer_cost, order.viewPrice))
        }
        list.forEach {
            val info = View.inflate(context, R.layout.view_order_info_item, null)
            info.findViewById<TextView>(R.id.orderInfoName).text = getString(it.first)
            val valueTextView = info.findViewById<TextView>(R.id.orderInfoValue)
            valueTextView.text = it.second
            if (getString(R.string.google_pay) == it.second && it.first == R.string.payment_method) {
                valueTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_google_pay_mark, 0)
            }
            infoLayout.addView(info)
        }
    }

    private fun getDefaultPayment(): String? {
        if (Preferences.getIsLastPaymentGooglePay(context)) return "${getString(R.string.google_pay)}\n${Preferences.getGooglePayInfo(context)}"
        val card = realm?.where(CreditCard::class.java)?.equalTo("isDefault", true)?.findFirst()
        cachedDefaultCardId = card?.id ?: 0L
        return if (card == null) null else String.format(Locale.getDefault(), "%s .... %s", card.type, card.lastDigits)
    }

    private fun showOrderNotification(order: Order) {
        val title = getString(R.string.your_transfer)
        val text = "${order.delivery?.title} - ${order.destination?.title}"
        val builder = android.support.v4.app.NotificationCompat.Builder(context, MessagingService.CHANNEL_ORDER)
                .setSmallIcon(R.mipmap.ic_launcher_rounded)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_rounded))
                .setContentTitle(title)
                .setContentText(text)
                .setShowWhen(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_SOUND)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val orderId = order.id?.toInt()
        val taskIntent = Intent(context, MainActivity_::class.java)
        taskIntent.putExtra("order_id", orderId)
        val contentIntent = PendingIntent.getActivity(context, 0, taskIntent, 0)
        val message = builder.setContentIntent(contentIntent)
                .build()
        manager.notify(orderId ?: 0, message)
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        //activity.makeToast(R.string.auth_error)
        activity.setResult(Activity.RESULT_FIRST_USER)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
    }
}
