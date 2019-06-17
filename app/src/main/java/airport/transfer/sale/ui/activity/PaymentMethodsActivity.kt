package airport.transfer.sale.ui.activity

import airport.transfer.sale.Constants
import airport.transfer.sale.R
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.drawer.PaymentMethodsFragment_
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import com.google.android.gms.wallet.PaymentData
import kotlinx.android.synthetic.main.toolbar.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_with_container)
open class PaymentMethodsActivity : BaseActivity() {

    @AfterViews
    fun ready() {
        home.setImageResource(R.drawable.back)
        home.setOnClickListener { onBackPressed() }
        showFragment(PaymentMethodsFragment_.builder().build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_GPAY -> {
                    data?.let {
                        val paymentData = PaymentData.getFromIntent(it)
                        val token = paymentData?.paymentMethodToken?.token
                        if (token == null) {
                            AlertDialog.Builder(this).setMessage("Google Pay token is null").setPositiveButton("OK", null).show()
                            return
                        }
                        /*val availableFields = "Available data: email=${paymentData?.email} userAddressName=${paymentData?.shippingAddress?.name} " +
                                "cardDescription=${paymentData?.cardInfo?.cardDescription} cardDetails=${paymentData?.cardInfo?.cardDetails} " +
                                "cardNetwork=${paymentData?.cardInfo?.cardNetwork} cardClass=${paymentData?.cardInfo?.cardClass} " +
                                "billingAddressName=${paymentData?.cardInfo?.billingAddress?.name} paymentMethodToken=$token" +
                                "${paymentData}"
                        AlertDialog.Builder(this).setMessage(availableFields).setPositiveButton("OK", null).show()*/
                        Preferences.saveGooglePayToken(this, token)
                        Preferences.saveGooglePayInfo(this, "${paymentData.cardInfo?.cardNetwork} .... ${paymentData.cardInfo?.cardDetails}")
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}