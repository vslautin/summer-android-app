package airport.transfer.sale.ui.fragment.drawer

import android.app.Activity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_personal_info.*
import kotlinx.android.synthetic.main.view_bottom_button.*
import airport.transfer.sale.*
import airport.transfer.sale.mvp.presenter.UserProfilePresenter
import airport.transfer.sale.mvp.view.UserProfileView
import airport.transfer.sale.rest.models.response.model.User
import airport.transfer.sale.storage.Preferences
import airport.transfer.sale.ui.activity.MainActivity
import airport.transfer.sale.ui.activity.PersonalInfoActivity
import airport.transfer.sale.ui.fragment.BaseFragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment

@EFragment(R.layout.fragment_personal_info)
open class PersonalInfoFragment : BaseFragment(), UserProfileView{

    @InjectPresenter
    lateinit var presenter: UserProfilePresenter

    var receiveEmails: Boolean? = null

    private var realm: Realm? = null

    private var nameObservable: Observable<Boolean>? = null
    private var emailObservable: Observable<Boolean>? = null
    private var lastNameObservable: Observable<Boolean>? = null

    @AfterViews
    fun ready(){
        realm = Realm.getDefaultInstance()
        bottomText.text = getString(R.string.save)
        getUser(realm)?.let { onUserReceived(it) }
        presenter.getUser(Preferences.getAuthToken(context))
        chooseButton.setOnClickListener {
            presenter.updateProfile(Preferences.getAuthToken(context)!!, nameEditText.text.toString(),
                    lastNameEditText.text.toString(), emailEditText.text.toString(), receiveEmails)
        }
        nameObservable = RxTextView.textChanges(nameEditText).map { it.isNotEmpty() }
        emailObservable = RxTextView.textChanges(emailEditText).map { it.isNotEmpty() }
        lastNameObservable = RxTextView.textChanges(lastNameEditText).map { it.isNotEmpty() }
        Observable.combineLatest(listOf(nameObservable, emailObservable, lastNameObservable), {
            notEmptyFields ->
            !notEmptyFields.contains(false)
        }).subscribe { valid -> chooseButton.isEnabled = valid }
        if (activity is PersonalInfoActivity) lastNameEditText.requestFocus()
    }

    override fun onProfileUpdated(user: User) {
        saveUser(realm, user)
        context.makeToast(R.string.profile_saved)
        hideKeyboard(context, view?.findFocus())
        (activity as? MainActivity)?.setupDrawerHeader()
        (activity as? PersonalInfoActivity)?.setResult(Activity.RESULT_OK)
        (activity as? PersonalInfoActivity)?.finish()
    }

    override fun onUserReceived(user: User) {
        saveUser(realm, user)
        receiveEmails = user.receiveEmails == 1
        user.firstName?.let { if (nameEditText.text.isEmpty()) nameEditText.setTextChars(it) }
        user.email?.let { if (emailEditText.text.isEmpty()) emailEditText.setText(it) }
        user.lastName?.let { if (lastNameEditText.text.isEmpty()) lastNameEditText.setText(it) }
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)
        if (Constants.TOKEN_ERROR_CODES.contains(code)) onTokenError()
    }

    override fun onTokenError() {
        //activity.makeToast(R.string.auth_error)
        (activity as? MainActivity)?.onLogout()
    }

    override fun onDestroy() {
        realm?.close()
        nameObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        emailObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        lastNameObservable?.unsubscribeOn(AndroidSchedulers.mainThread())
        super.onDestroy()
    }
}
