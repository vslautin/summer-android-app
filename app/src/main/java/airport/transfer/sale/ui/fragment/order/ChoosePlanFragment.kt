package airport.transfer.sale.ui.fragment.order

import airport.transfer.sale.*
import airport.transfer.sale.adapter.PlansAdapter
import airport.transfer.sale.adapter.ViewHolderWrapper
import airport.transfer.sale.mvp.presenter.PricePresenter
import airport.transfer.sale.mvp.presenter.ServicePlanPresenter
import airport.transfer.sale.mvp.view.PriceView
import airport.transfer.sale.mvp.view.ServicePlanView
import airport.transfer.sale.rest.models.response.PlansResponse
import airport.transfer.sale.ui.activity.PaymentMethodsActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import airport.transfer.sale.ui.view.plan.PromocodeView
import airport.transfer.sale.ui.view.plan.PromocodeView_
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_plans.*
import kotlinx.android.synthetic.main.view_promocode.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_plans)
open class ChoosePlanFragment : BaseFragment(), ServicePlanView, PriceView {

    @InjectPresenter
    lateinit var plansPresenter: ServicePlanPresenter

    lateinit var plansAdapter: PlansAdapter

    @InjectPresenter
    lateinit var pricePresenter: PricePresenter

    private var realm: Realm? = null

    //private var mPromoView: EditText? = null

    private lateinit var textWatcher: TextWatcher
    private var mRunnable: Runnable? = null
    private var promoView: PromocodeView? = null

    @AfterViews
    fun ready(){
        realm = Realm.getDefaultInstance()
        plansPresenter.getPlans()
        promoView = PromocodeView_.build(context)
        rootLayout.addView(promoView, 0)
        textWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                promoView?.removeCallbacks(mRunnable)
                mRunnable = Runnable {
                    plansPresenter.getPlans()
                }
                promoView?.postDelayed(mRunnable, 600)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        promoEditText.addTextChangedListener(textWatcher)
        plansAdapter = PlansAdapter(GlideImageLoader()/*, ::onPromoAvailable*/)
        /*val plans  = Preferences.getPlans(context)
        plans?.let { onPlansReceived(PlansResponse(Constants.Status.SUCCESS, 200, null, it)) }*/
        with(recyclerView){
            layoutManager = LinearLayoutManager(context)
            adapter = plansAdapter
            val decor = AsymDividerItemDecoration(context, context.dip(17), 0, false, false)
            addItemDecoration(decor)
        }
        plansAdapter.setClickListener(object: ViewHolderWrapper.ClickListener{
            override fun onItemClicked(position: Int, wrapper: ViewHolderWrapper<*>) {
                val savedOrder = getCurrentOrder(realm)
                val plan = plansAdapter.getItem(position)
                savedOrder.plan = plan
                saveCurrentOrder(realm, savedOrder)
                val viewPrice = if (plan.coupon != null) plan.coupon?.viewPrice else plan.viewPrice
                onPriceReceived(viewPrice!!)
            }
        })
        promoEditText?.clearFocus()
        smallEditText?.requestFocus()
    }

    /*private fun onPromoAvailable(editText: EditText){
        if (mPromoView != null) mPromoView?.removeTextChangedListener(textWatcher)
        mPromoView = editText
        editText.addTextChangedListener(textWatcher)
    }*/

    override fun onPlansReceived(plans: PlansResponse) {
        plansAdapter.replaceAll(plans.plans)
        val discount = plans.plans.getOrNull(0)?.coupon?.discount ?: 0
        promoView?.bind(discount)
    }

    override fun onPriceReceived(price: Int) {
        val savedOrder = getCurrentOrder(realm)
        //savedOrder.cost = price
        saveCurrentOrder(realm, savedOrder)
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    fun onPriceReceived(price: String) {
        val savedOrder = getCurrentOrder(realm)
        savedOrder.viewPrice = price
        savedOrder.couponName = promoEditText.text.toString()
        saveCurrentOrder(realm, savedOrder)
        if (Constants.needGPay) startActivityForResult(Intent(context, PaymentMethodsActivity_::class.java), Constants.REQUEST_PAYMENT)
        else {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_PAYMENT) {
                activity.setResult(Activity.RESULT_OK)
                activity.finish()
            }
        }
    }

    override fun onError(code: Int, message: String) {
        AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener { activity.finish() }
                .show()
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}