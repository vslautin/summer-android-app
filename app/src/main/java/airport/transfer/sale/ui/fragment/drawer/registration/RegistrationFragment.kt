package airport.transfer.sale.ui.fragment.drawer.registration

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.mvp.presenter.AuthPresenter
import airport.transfer.sale.mvp.presenter.SplashPresenter
import airport.transfer.sale.mvp.view.AuthView
import airport.transfer.sale.mvp.view.SplashView
import airport.transfer.sale.rest.models.response.TokenResponse
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.RegistrationActivity
import airport.transfer.sale.ui.activity.WebViewActivity_
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_registration)
open class RegistrationFragment : BaseFragment(), AuthView, SplashView {

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    @InjectPresenter
    lateinit var splashPresenter: SplashPresenter

    private var realm: Realm? = null

    private var mFirstFieldObservable: Observable<CharSequence>? = null
    private var mSecondFieldObservable: Observable<CharSequence>? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        realm = Realm.getDefaultInstance()
        super.onViewCreated(view, savedInstanceState)
        val phoneMask = getString(R.string.phone_mask)
        firstText.text = getString(R.string.phone)
        firstFieldText.mask = phoneMask
        firstFieldText.inputType = InputType.TYPE_CLASS_PHONE
        secondText.text = getString(R.string.code)
        secondFieldText.mask = getString(R.string.code_mask)
        spamCheckBox.visibility = View.GONE
        bottomText.text = getString(R.string.get_sms)
        secondFieldText.inputType = InputType.TYPE_CLASS_NUMBER
        secondFieldText.isEnabled = false
        nextButton.setOnClickListener {
            secondFieldText.requestFocus()
            firstFieldText.isEnabled = false
            presenter.getCode(firstFieldText.text.replace("[^\\d]".toRegex(), ""))
            bottomText.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
        mFirstFieldObservable = RxTextView.textChanges(firstFieldText).observeOn(AndroidSchedulers.mainThread())
        mFirstFieldObservable?.subscribe { nextButton?.isEnabled = it.length == phoneMask.length }
        termsOfUse.text = getString(R.string.registration_terms_of_use).fromHtmlCompat()
        termsOfUse.setOnClickListener {
            val webIntent = Intent(context, WebViewActivity_::class.java)
            webIntent.putExtra(Constants.EXTRA_KEY, getHowToUseLink())
            startActivity(webIntent)
        }
        privacyText.text = getString(R.string.i_accept_user_agreement).fromHtmlCompat()
        privacyText.setOnClickListener {
            val webIntent = Intent(context, WebViewActivity_::class.java)
            webIntent.putExtra(Constants.EXTRA_KEY, getTermsLink())
            startActivity(webIntent)
        }
    }

    override fun onPhoneSent(phone: String) {
        bottomText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        bottomText.text = getString(R.string.authorize)
        waitCodeText.visibility = View.VISIBLE
        secondFieldText.isEnabled = true
        mSecondFieldObservable = RxTextView.textChanges(secondFieldText).observeOn(AndroidSchedulers.mainThread())
        mSecondFieldObservable?.subscribe {
                    nextButton.isEnabled = it.length == getString(R.string.code_mask).length
                    bottomText.isEnabled = it.length == getString(R.string.code_mask).length
                }
        nextButton.setOnClickListener {
            if (!privacyCheckBox.isChecked) {
                Toast.makeText(context, R.string.accept_user_agreement, Toast.LENGTH_LONG).show()
                hideKeyboard(context, secondFieldText)
            } else {
                presenter.getToken(phone, secondFieldText.text.toString())
                bottomText.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onTokenReceived(response: TokenResponse) {
        bottomText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        Preferences.saveToken(response.token, context)
        val userEmail = response.user.email
        val userName = response.user.firstName
        saveUser(realm, response.user)
        Preferences.getPushToken(context)?.let { splashPresenter.addPushToken(response.token, it) }
        (activity as RegistrationActivity).showRegistrationNextStep(userName, userEmail)
    }

    override fun onCodeDenied(message: String?) {
        bottomText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (code == 403) onCodeDenied(message)
    }

    override fun onDestroy() {
        realm?.close()
        mFirstFieldObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        mSecondFieldObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        super.onDestroy()
    }
}