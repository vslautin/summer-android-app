package airport.transfer.sale.ui.fragment.drawer.registration

import airport.transfer.sale.*
import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.mvp.presenter.UserProfilePresenter
import airport.transfer.sale.mvp.view.UserProfileView
import airport.transfer.sale.rest.models.response.model.User
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_registration)
open class UserEmailFragment : BaseFragment(), UserProfileView {

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"

        fun newInstance(name: String?, email: String?): UserEmailFragment {
            val args = Bundle()
            name?.let { args.putString(NAME, it) }
            email?.let { args.putString(EMAIL, it) }
            val fragment = UserEmailFragment_()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: UserProfilePresenter

    private var realm: Realm? = null

    @AfterViews
    fun ready() {
        realm = Realm.getDefaultInstance()
        privacyLayout.visibility = View.GONE
        firstText.text = getString(R.string.name_personal)
        secondText.text = getString(R.string.email)
        lastNameLayout.visibility = View.VISIBLE
        arguments.getString(NAME)?.let { firstFieldText.setText(it) }
        arguments.getString(EMAIL)?.let { secondFieldText.setText(it) }
        spamCheckBox.visibility = View.VISIBLE
        termsOfUse.visibility = View.GONE
        bottomText.text = getString(R.string.save)
        secondFieldText.inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        /*val nameObservable = RxTextView.textChanges(firstFieldText).map { it.isNotEmpty() }
        val emailObservable = RxTextView.textChanges(secondFieldText).map { it.isNotEmpty() }
        Observable.combineLatest(listOf(nameObservable, emailObservable), {
            notEmptyFields ->
            !notEmptyFields.contains(false)
        }).subscribe { valid -> nextButton.isEnabled = valid }*/
        nextButton.setOnClickListener {
            if (firstFieldText.text.isNotEmpty() && isValidEmail(secondFieldText.text.toString()) && lastNameEditText.text.isNotEmpty()) {
                presenter.updateProfile(Preferences.getAuthToken(context)!!, firstFieldText.text.toString(),
                        lastNameEditText.text.toString(), secondFieldText.text.toString(), spamCheckBox.isChecked)
            } else context.makeToast(R.string.fill_all_fields)
        }
    }

    override fun onUserReceived(user: User) {
    }

    override fun onProfileUpdated(user: User) {
        saveUser(realm, user)
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        activity.makeToast(R.string.auth_error)
    }

    override fun onDestroy() {
        realm?.close()
        super.onDestroy()
    }
}