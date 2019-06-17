package airport.transfer.sale.ui.fragment.order

import android.app.Activity
import android.content.Intent
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_credit_card.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.mvp.model.CreditCard
import airport.transfer.sale.mvp.presenter.PaymentPresenter
import airport.transfer.sale.mvp.view.PaymentView
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.WebViewActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import ru.cloudpayments.sdk.CardFactory
import java.util.*

@EFragment(R.layout.fragment_credit_card)
open class CreditCardFragment : BaseFragment(), PaymentView {

    @InjectPresenter
    lateinit var presenter: PaymentPresenter

    private var realm: Realm? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        bottomText.text = getString(R.string.add_card)
        addButton.setOnClickListener {
            if (isFieldsValid()) {
                addButton.isEnabled = false
                presenter.getPublicId(Preferences.getAuthToken(context)!!)
                progressBar.visibility = View.VISIBLE
                bottomText.visibility = View.GONE
            }
        }
        paymentInfoButton.setOnClickListener {
            val webIntent = Intent(context, WebViewActivity_::class.java)
            webIntent.putExtra(Constants.EXTRA_KEY, getPaymentLink())
            startActivity(webIntent)
        }
    }

    override fun onPublicIdReceived(publicId: String) {
        val holder = cardHolderEditText.text.toString().trim()
        val number = cardNumberEditText.text.toString().replace(" ", "")
        val date = String.format(Locale.ENGLISH, "%s %s", cardMonthEditText.text.toString(),
                cardYearEditText.text.toString()).replace(" ", "")
        val cvv = cvcEditText.text.toString().trim()
        val crypto: String?
        try {
            crypto = CardFactory.create(number, date, cvv).cardCryptogram(publicId)
        } catch (e: Exception) {
            context.makeToast("Can not create cryptogram")
            return
        }
        presenter.addCard(Preferences.getAuthToken(context)!!, holder, crypto, number)
    }

    override fun onCreditCardAdded(card: CreditCard) {
        realm?.let {
            if (!it.isClosed) it.executeTransaction {
                it.copyToRealmOrUpdate(card)
            }
        }
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun isFieldsValid(): Boolean {
        var valid = true
        val number = cardNumberEditText.text.toString().replace(" ", "")
        if (number.length < 13) {
            context.makeToast(R.string.please_fill_card_number)
            return false
        }
        val holder = cardHolderEditText.text.toString().trim()
        val date = String.format(Locale.ENGLISH, "%s %s", cardMonthEditText.text.toString(),
                cardYearEditText.text.toString()).replace(" ", "")
        val cvv = cvcEditText.text.toString().trim()
        valid = valid && CardFactory.create(number, date, cvv).isValidNumber
        valid = valid && holder.isNotEmpty()
        valid = valid && isDateValid()
        valid = valid && cvv.isNotEmpty()
        if (cvv.isEmpty()) context.makeToast(R.string.not_valid_cvv)
        else if (!isDateValid()) context.makeToast(R.string.not_valid_date)
        else if (!valid) context.makeToast(R.string.not_valid_card)
        return valid
    }

    private fun isDateValid(): Boolean {
        val calendar = Calendar.getInstance()
        if (cardYearEditText.text.isEmpty() || cardMonthEditText.text.isEmpty()) return false
        val year = cardYearEditText.text.toString().trim().toInt()
        if (2000 + year < calendar.get(Calendar.YEAR)) return false
        val month = cardMonthEditText.text.toString().trim().toInt()
        if (month == 0 || month > 12) return false
        calendar.set(2000 + year, month - 1, 1)
        return Calendar.getInstance().before(calendar)
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}